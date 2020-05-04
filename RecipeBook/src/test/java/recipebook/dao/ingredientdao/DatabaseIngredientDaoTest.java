package recipebook.dao.ingredientDao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import recipebook.dao.DataStoreConnector;
import recipebook.dao.DatabaseConnector;
import recipebook.dao.DatabaseException;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.domain.ingredient.Ingredient;

public class DatabaseIngredientDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    IngredientDao ingredientDao;
    DataStoreConnector connector;
    Connection connection;

    @Before
    public void setUp() throws DatabaseException {
        connector = new DatabaseConnector(testFolder.getRoot().toString() + "/");
        connector.initializeDataStore();
        ingredientDao = connector.getIngredientDao();
    }

    @After
    public void finalize() throws DatabaseException {
        connector.closeDataStore();
    }

    @Test
    public void createdIngredientHasRightName() {
        Ingredient ingredient = ingredientDao.create(new Ingredient("chicken"));
        assertThat(ingredient.getName(), is(equalTo("chicken")));
    }

    @Test
    public void createdIngredientHasRightId() {
        Ingredient ingredient = ingredientDao.create(new Ingredient("tomato"));
        assertThat(ingredient.getId(), is(1));

        ingredient = ingredientDao.create(new Ingredient("potato"));
        assertThat(ingredient.getId(), is(2));
    }

    @Test
    public void getAllReturnsRightNumberOfIngredients() {
        ingredientDao.create(new Ingredient("banana"));
        ingredientDao.create(new Ingredient("apple"));
        ingredientDao.create(new Ingredient("kiwi"));

        List<Ingredient> ingredients = ingredientDao.getAll();
        
        assertThat(ingredients.size(), is(3));
    }

    @Test
    public void getByNameAndUnitReturnsRightIngredientWhenFound() {
        ingredientDao.create(new Ingredient("salmon"));
        ingredientDao.create(new Ingredient("milk"));

        Ingredient ingredient = ingredientDao.getByNameAndUnit("salmon", "g");
        
        assertThat(ingredient.getName(), is(equalTo("salmon")));
    }

    @Test
    public void getByNameAndUnitReturnsNullIfNotFound() {
        ingredientDao.create(new Ingredient("salmon"));

        Ingredient ingredient = ingredientDao.getByNameAndUnit("chicken", "g");
        
        assertThat(ingredient, is(nullValue()));
    }

    @Test
    public void getByIdReturnsRightIngredient() {
        ingredientDao.create(new Ingredient("meat"));
        ingredientDao.create(new Ingredient("fish"));
        
        Ingredient ingredient = ingredientDao.getById(1);
        assertThat(ingredient.getName(), is(equalTo("meat")));
        
        ingredient = ingredientDao.getById(2);
        assertThat(ingredient.getName(), is(equalTo("fish")));
    }

    @Test
    public void getByIdReturnsNullIfNotFound() {
        ingredientDao.create(new Ingredient("salmon"));
        Ingredient ingredient = ingredientDao.getById(2);
        assertThat(ingredient, is(nullValue()));
    }

    @Test
    public void rightIngredientsAreLoadedWhenObjectInstantiated() {
        ingredientDao.create(new Ingredient("salmon"));
        ingredientDao.create(new Ingredient("milk"));
        ingredientDao.create(new Ingredient("chicken"));
        IngredientDao newIngDao = connector.getIngredientDao();
        List<Ingredient> ingredients = newIngDao.getAll();
        List<String> ingredientNames = ingredients.stream().map(i -> i.getName()).collect(Collectors.toList());
        assertTrue(ingredientNames.contains("salmon"));
        assertTrue(ingredientNames.contains("milk"));
        assertTrue(ingredientNames.contains("chicken"));
    }
}
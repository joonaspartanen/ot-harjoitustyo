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
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.domain.ingredient.Ingredient;

public class DatabaseIngredientDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    IngredientDao ingDao;
    DataStoreConnector connector;
    Connection connection;

    @Before
    public void setUp() {
        connector = new DatabaseConnector(testFolder.getRoot().toString() +"/");
        connector.initializeDataStore();
        ingDao = connector.getIngredientDao();
    }

    @After
    public void finalize() {
        connector.closeDataStore();
    }

    @Test
    public void createdIngredientHasRightName() {
        Ingredient ingredient = ingDao.create(new Ingredient("chicken"));
        assertThat(ingredient.getName(), is(equalTo("chicken")));
    }

    @Test
    public void createdIngredientHasRightId() {
        Ingredient ingredient = ingDao.create(new Ingredient("tomato"));
        assertThat(ingredient.getId(), is(1));
        ingredient = ingDao.create(new Ingredient("potato"));
        assertThat(ingredient.getId(), is(2));
    }

    @Test
    public void getAllReturnsRightNumberOfIngredients() {
        ingDao.create(new Ingredient("banana"));
        ingDao.create(new Ingredient("apple"));
        ingDao.create(new Ingredient("kiwi"));
        List<Ingredient> ingredients = ingDao.getAll();
        assertThat(ingredients.size(), is(3));
    }

    @Test
    public void getByNameAndUnitReturnsRightIngredientWhenFound() {
        ingDao.create(new Ingredient("salmon"));
        ingDao.create(new Ingredient("milk"));
        Ingredient ingredient = ingDao.getByNameAndUnit("salmon", "g");
        assertThat(ingredient.getName(), is(equalTo("salmon")));
    }

    @Test
    public void getByNameAndUnitReturnsNullIfNotFound() {
        ingDao.create(new Ingredient("salmon"));
        Ingredient ingredient = ingDao.getByNameAndUnit("chicken", "g");
        assertThat(ingredient, is(nullValue()));
    }

    @Test
    public void getByIdReturnsRightIngredient() {
        ingDao.create(new Ingredient("meat"));
        ingDao.create(new Ingredient("fish"));
        Ingredient ingredient = ingDao.getById(1);
        assertThat(ingredient.getName(), is(equalTo("meat")));
        ingredient = ingDao.getById(2);
        assertThat(ingredient.getName(), is(equalTo("fish")));
    }

    @Test
    public void getByIdReturnsNullIfNotFound() {
        ingDao.create(new Ingredient("salmon"));
        Ingredient ingredient = ingDao.getById(2);
        assertThat(ingredient, is(nullValue()));
    }

    @Test
    public void rightIngredientsAreLoadedWhenObjectInstantiated() {
        ingDao.create(new Ingredient("salmon"));
        ingDao.create(new Ingredient("milk"));
        ingDao.create(new Ingredient("chicken"));
        IngredientDao newIngDao = connector.getIngredientDao();
        List<Ingredient> ingredients = newIngDao.getAll();
        List<String> ingredientNames = ingredients.stream().map(i -> i.getName()).collect(Collectors.toList());
        assertTrue(ingredientNames.contains("salmon"));
        assertTrue(ingredientNames.contains("milk"));
        assertTrue(ingredientNames.contains("chicken"));
    }
}
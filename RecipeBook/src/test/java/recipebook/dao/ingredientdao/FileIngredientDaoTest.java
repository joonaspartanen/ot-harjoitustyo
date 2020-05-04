package recipebook.dao.ingredientdao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import recipebook.dao.DataStoreException;
import recipebook.domain.ingredient.Ingredient;

public class FileIngredientDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    private IngredientDao ingDao;
    private File testFile;

    @Before
    public void setUp() {
        try {
            testFile = testFolder.newFile("testIngredients.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ingDao = new FileIngredientDao(testFile.getAbsolutePath());
    }

    @Test
    public void createdIngredientHasRightName() throws DataStoreException {
        Ingredient ingredient = ingDao.create(new Ingredient("chicken"));
        assertThat(ingredient.getName(), is(equalTo("chicken")));
    }

    @Test
    public void createdIngredientHasRightId() throws DataStoreException {
        Ingredient ingredient = ingDao.create(new Ingredient("tomato"));
        assertThat(ingredient.getId(), is(1));
        ingredient = ingDao.create(new Ingredient("potato"));
        assertThat(ingredient.getId(), is(2));
    }

    @Test
    public void getAllReturnsRightNumberOfIngredients() throws DataStoreException {
        ingDao.create(new Ingredient("banana"));
        ingDao.create(new Ingredient("apple"));
        ingDao.create(new Ingredient("kiwi"));
        List<Ingredient> ingredients = ingDao.getAll();
        assertThat(ingredients.size(), is(3));
    }

    @Test
    public void getByNameReturnsRightIngredientWhenFound() throws DataStoreException {
        ingDao.create(new Ingredient("salmon"));
        ingDao.create(new Ingredient("milk"));
        List<Ingredient> ingredients = ingDao.getByName("salmon");
        assertThat(ingredients.get(0).getName(), is(equalTo("salmon")));
    }

    @Test
    public void getByNameReturnsEmptyListIfNotFound() throws DataStoreException {
        ingDao.create(new Ingredient("salmon"));
        List<Ingredient> ingredients = ingDao.getByName("chicken");
        assertTrue(ingredients.isEmpty());
    }

    @Test
    public void getByIdReturnsRightIngredient() throws DataStoreException {
        ingDao.create(new Ingredient("meat"));
        ingDao.create(new Ingredient("fish"));
        Ingredient ingredient = ingDao.getById(1);
        assertThat(ingredient.getName(), is(equalTo("meat")));
        ingredient = ingDao.getById(2);
        assertThat(ingredient.getName(), is(equalTo("fish")));
    }

    @Test
    public void getByIdReturnsThrowsExceptionIfNotFound() throws DataStoreException {
        exceptionRule.expect(DataStoreException.class);
        exceptionRule.expectMessage("Ingredient with id 2 was not found.");

        ingDao.create(new Ingredient("salmon"));
        ingDao.getById(2);
    }

    @Test
    public void rightIngredientsAreLoadedWhenObjectInstantiated() throws DataStoreException {
        ingDao.create(new Ingredient("salmon"));
        ingDao.create(new Ingredient("milk"));
        ingDao.create(new Ingredient("chicken"));
        IngredientDao newIngDao = new FileIngredientDao(testFile.getAbsolutePath());
        List<Ingredient> ingredients = newIngDao.getAll();
        List<String> ingredientNames = ingredients.stream().map(i -> i.getName()).collect(Collectors.toList());
        assertTrue(ingredientNames.contains("salmon"));
        assertTrue(ingredientNames.contains("milk"));
        assertTrue(ingredientNames.contains("chicken"));
    }

    @Test
    public void getByNameAndUnitReturnsRightIngredientWhenFound() throws DataStoreException {
        ingDao.create(new Ingredient("salmon"));
        ingDao.create(new Ingredient("milk"));
        Ingredient ingredient = ingDao.getByNameAndUnit("salmon", "g");
        assertThat(ingredient.getName(), is(equalTo("salmon")));
    }

    @Test
    public void getByNameAndUnitReturnsNullIfNotFound() throws DataStoreException {
        ingDao.create(new Ingredient("salmon"));
        Ingredient ingredient = ingDao.getByNameAndUnit("chicken", "g");
        assertThat(ingredient, is(nullValue()));
    }

}

package recipebook.dao.recipedao;

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

import recipebook.TestHelper;
import recipebook.dao.DataStoreException;
import recipebook.dao.ingredientdao.IngredientDaoMock;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.userdao.UserDao;
import recipebook.domain.recipe.Recipe;
import recipebook.dao.userdao.UserDaoMock;
import recipebook.dao.userdao.UserNotFoundException;

public class FileRecipeDaoTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private RecipeDao recipeDao;
    private IngredientDao ingredientDaoMock;
    private UserDao userDaoMock;
    private File testRecipeFile;
    private File testRecipesIngsFile;
    private File testFavoriteRecipesFile;
    private TestHelper helper;

    @Before
    public void setUp() throws DataStoreException, UserNotFoundException {
        try {
            testRecipeFile = testFolder.newFile("testRecipes.txt");
            testRecipesIngsFile = testFolder.newFile("testRecipesIngs.txt");
            testFavoriteRecipesFile = testFolder.newFile("testFavoriteRecipes.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ingredientDaoMock = new IngredientDaoMock();
        userDaoMock = new UserDaoMock();
        recipeDao = new FileRecipeDao(ingredientDaoMock, userDaoMock, testRecipeFile.getAbsolutePath(),
                testRecipesIngsFile.getAbsolutePath(), testFavoriteRecipesFile.getAbsolutePath());
        helper = new TestHelper(ingredientDaoMock, userDaoMock);
    }

    @Test
    public void createdRecipeHasRightName() throws DataStoreException {
        Recipe recipe = recipeDao.create(helper.createTestRecipe("Salmon soup"));

        assertThat(recipe.getName(), is(equalTo("Salmon soup")));
    }

    @Test
    public void createdRecipeHasRightId() throws DataStoreException {
        helper.initializeRecipeBook(4, recipeDao);

        Recipe recipe = recipeDao.create(helper.createTestRecipe("Chicken tikka"));

        assertThat(recipe.getId(), is(5));
    }

    @Test
    public void createdRecipeHasRightAuthor() throws DataStoreException {
        Recipe recipe = recipeDao.create(helper.createTestRecipe("Salmon soup"));

        assertThat(recipe.getAuthorId(), is(helper.getTestUser().getId()));
    }

    @Test
    public void getAllReturnsRightNumberOfRecipes() throws DataStoreException, UserNotFoundException {
        helper.initializeRecipeBook(3, recipeDao);

        List<Recipe> recipes = recipeDao.getAll();

        assertThat(recipes.size(), is(3));
    }

    @Test
    public void getByNameReturnsAllMatchingRecipes() throws DataStoreException, UserNotFoundException {
        helper.initializeRecipeBook(3, recipeDao);

        recipeDao.create(helper.createTestRecipe("Chicken soup"));
        recipeDao.create(helper.createTestRecipe("Salmon soup"));
        recipeDao.create(helper.createTestRecipe("Noodles"));

        List<Recipe> foundRecipes = recipeDao.getByName("soup");
        List<String> recipeNames = foundRecipes.stream().map(r -> r.getName()).collect(Collectors.toList());
        assertThat(recipeNames.size(), is(2));
        assertThat(recipeNames, hasItem("Chicken soup"));
        assertThat(recipeNames, hasItem("Salmon soup"));
    }

    @Test
    public void getByNameReturnsEmptyListIfRecipesNotFound() throws DataStoreException, UserNotFoundException {
        helper.initializeRecipeBook(3, recipeDao);

        List<Recipe> foundRecipes = recipeDao.getByName("Knish");

        assertTrue(foundRecipes.isEmpty());
    }

    @Test
    public void getByIdReturnsRightRecipe() throws DataStoreException, UserNotFoundException {
        helper.initializeRecipeBook(3, recipeDao);
        recipeDao.create(helper.createTestRecipe("Meatballs"));

        Recipe recipe = recipeDao.getById(4);

        assertThat(recipe.getName(), is(equalTo("Meatballs")));
    }

    @Test
    public void getByIdThrowsExceptionIfRecipeNotFound() throws DataStoreException, UserNotFoundException {
        exceptionRule.expect(DataStoreException.class);
        exceptionRule.expectMessage("Recipe with id 4 was not found.");

        helper.initializeRecipeBook(3, recipeDao);
        recipeDao.getById(4);
    }

    @Test
    public void getByIngredientReturnsAllMatchingRecipes() throws DataStoreException, UserNotFoundException {
        recipeDao.create(helper.createTestRecipeWithIngredients("Salmon soup", "salmon", "milk", "butter", "potato"));
        recipeDao.create(helper.createTestRecipeWithIngredients("Roasted fish", "salmon", "lemon", "oil", "salt"));
        recipeDao.create(
                helper.createTestRecipeWithIngredients("Butter chicken", "chicken", "butter", "cream", "tomato"));
        recipeDao.create(helper.createTestRecipeWithIngredients("Chicken soup", "chicken", "onion", "carrot"));

        List<Recipe> foundRecipes = recipeDao.getByIngredient("butter");

        List<String> recipeNames = foundRecipes.stream().map(r -> r.getName()).collect(Collectors.toList());
        assertThat(recipeNames.size(), is(2));
        assertThat(recipeNames, hasItem("Salmon soup"));
        assertThat(recipeNames, hasItem("Butter chicken"));
    }

    @Test
    public void rightRecipesAreLoadedWhenObjectInstantiated() throws DataStoreException, UserNotFoundException {
        recipeDao.create(helper.createTestRecipe("Meatballs"));
        recipeDao.create(helper.createTestRecipe("Salmon soup"));
        recipeDao.create(helper.createTestRecipe("Pasta carbonara"));

        RecipeDao newRecipeDao = new FileRecipeDao(ingredientDaoMock, userDaoMock,
                testRecipeFile.getAbsolutePath(), testRecipesIngsFile.getAbsolutePath(),
                testFavoriteRecipesFile.getAbsolutePath());
        List<Recipe> recipes = newRecipeDao.getAll();

        List<String> recipeNames = recipes.stream().map(r -> r.getName()).collect(Collectors.toList());
        assertTrue(recipeNames.contains("Meatballs"));
        assertTrue(recipeNames.contains("Salmon soup"));
        assertTrue(recipeNames.contains("Pasta carbonara"));
    }

    @Test
    public void deleteRemovesRecipe() throws DataStoreException, UserNotFoundException {
        Recipe meatballs = recipeDao.create(helper.createTestRecipe("Meatballs"));
        assertThat(recipeDao.getAll(), hasItem(meatballs));

        recipeDao.delete(meatballs);

        assertThat(recipeDao.getAll(), not(hasItem(meatballs)));
    }
}

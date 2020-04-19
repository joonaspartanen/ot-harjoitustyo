package recipebook.dao.recipeDao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import recipebook.TestHelper;
import recipebook.dao.DataStoreConnector;
import recipebook.dao.DatabaseConnector;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.DatabaseRecipeDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.domain.recipe.Recipe;

public class DatabaseRecipeDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    RecipeDao recipeDao;
    IngredientDao ingDaoMock;
    DataStoreConnector connector;
    Connection connection;
    TestHelper helper;

    @Before
    public void setUp() {
        connector = new DatabaseConnector(testFolder.getRoot().toString() +"/");
        connector.initializeDataStore();
        ingDaoMock = connector.getIngredientDao();
        recipeDao = connector.getRecipeDao();
        helper = new TestHelper(ingDaoMock);
    }

    @After
    public void finalize() {
        connector.closeDataStore();
    }

    @Test
    public void createdRecipeHasRightName() {
        Recipe recipe = recipeDao.create(helper.createTestRecipe("Salmon soup"));
        assertThat(recipe.getName(), is(equalTo("Salmon soup")));
    }

    @Test
    public void createdRecipeHasRightId() {
        helper.initializeRecipeBook(4, recipeDao);
        Recipe recipe = recipeDao.create(helper.createTestRecipe("Chicken tikka"));
        assertThat(recipe.getId(), is(5));
    }

    @Test
    public void getAllReturnsRightNumberOfRecipes() {
        helper.initializeRecipeBook(3, recipeDao);
        List<Recipe> recipes = recipeDao.getAll();
        assertThat(recipes.size(), is(3));
    }

    @Test
    public void getByIdReturnsRightRecipe() {
        Recipe fishSoup = helper.createTestRecipe("Fish soup");
        Recipe returnedRecipe = recipeDao.create(fishSoup);
        assertThat(returnedRecipe.getId(), is(1));
        Recipe foundRecipe = recipeDao.getById(1);
        assertThat(foundRecipe.getId(), is(1));
        assertThat(foundRecipe, is(equalTo(fishSoup)));
    }

    @Test
    public void getByIdReturnsNullIfRecipeNotFound() {
        helper.initializeRecipeBook(3, recipeDao);
        Recipe recipe = recipeDao.getById(4);
        assertThat(recipe, is(nullValue()));
    }

    @Test
    public void createdRecipeHasRightIngredients() {
        Recipe recipe = helper.createTestRecipeWithIngredients("Fish tacos", "cod", "tortillas", "salsa verde");
        recipeDao.create(recipe);
        Recipe foundRecipe = recipeDao.getById(1);
        List<String> ingredientNames = foundRecipe.getIngredients().keySet().stream().map(i -> i.getName())
                .collect(Collectors.toList());
        assertThat(ingredientNames, hasItem("cod"));
        assertThat(ingredientNames, hasItem("tortillas"));
        assertThat(ingredientNames, hasItem("salsa verde"));
    }

    @Test
    public void getByNameReturnsAllMatchingRecipes() {
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
    public void getByNameReturnsEmptyListIfRecipesNotFound() {
        helper.initializeRecipeBook(3, recipeDao);
        List<Recipe> foundRecipes = recipeDao.getByName("Knish");
        assertTrue(foundRecipes.isEmpty());
    }

    @Test
    public void rightRecipesAreLoadedWhenObjectInstantiated() {
        recipeDao.create(helper.createTestRecipe("Meatballs"));
        recipeDao.create(helper.createTestRecipe("Salmon soup"));
        recipeDao.create(helper.createTestRecipe("Pasta carbonara"));

        RecipeDao newRecipeDao = connector.getRecipeDao();
        List<Recipe> recipes = newRecipeDao.getAll();
        List<String> recipeNames = recipes.stream().map(r -> r.getName()).collect(Collectors.toList());
        assertTrue(recipeNames.contains("Meatballs"));
        assertTrue(recipeNames.contains("Salmon soup"));
        assertTrue(recipeNames.contains("Pasta carbonara"));
    }

    @Test
    public void getByIngredientReturnsAllMatchingRecipes() {
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
    public void getByIngredientReturnsEmptyListIfNoMatchesFound() {
        recipeDao.create(helper.createTestRecipeWithIngredients("Salmon soup", "salmon", "milk", "butter", "potato"));
        recipeDao.create(helper.createTestRecipeWithIngredients("Roasted fish", "salmon", "lemon", "oil", "salt"));
        recipeDao.create(
                helper.createTestRecipeWithIngredients("Butter chicken", "chicken", "butter", "cream", "tomato"));
        recipeDao.create(helper.createTestRecipeWithIngredients("Chicken soup", "chicken", "onion", "carrot"));
        List<Recipe> foundRecipes = recipeDao.getByIngredient("avocado");
        assertTrue(foundRecipes.isEmpty());
    }

    @Test
    public void deleteRemovesRecipe() {
        Recipe salmonSoup = recipeDao.create(helper.createTestRecipe("Salmon soup"));
        recipeDao.create(helper.createTestRecipe("Chicken soup"));

        recipeDao.delete(salmonSoup);

        List<Recipe> foundRecipes = recipeDao.getAll();
        assertThat(foundRecipes.size(), is(1));
        assertThat(foundRecipes, not(hasItem(salmonSoup)));
    }

}
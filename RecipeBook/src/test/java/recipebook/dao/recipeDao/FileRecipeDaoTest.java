package recipebook.dao.recipeDao;

import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.recipedao.FileRecipeDao;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.ingredientdao.ArrayListIngredientDao;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import recipebook.TestHelper;
import recipebook.domain.recipe.Recipe;

public class FileRecipeDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private RecipeDao recipeDao;
    private IngredientDao ingDaoMock;
    private File testRecipeFile;
    private File testRecipesIngsFile;
    private TestHelper helper;

    @Before
    public void setUp() {
        try {
            testRecipeFile = testFolder.newFile("testRecipes.txt");
            testRecipesIngsFile = testFolder.newFile("testRecipesIngs.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ingDaoMock = new ArrayListIngredientDao();
        recipeDao = new FileRecipeDao(ingDaoMock, testRecipeFile.getAbsolutePath(),
                testRecipesIngsFile.getAbsolutePath());
        helper = new TestHelper();
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
    public void getByIdReturnsRightRecipe() {
        helper.initializeRecipeBook(3, recipeDao);
        recipeDao.create(helper.createTestRecipe("Meatballs"));
        Recipe recipe = recipeDao.getById(4);
        assertThat(recipe.getName(), is(equalTo("Meatballs")));
    }

    @Test
    public void getByIdReturnsNullIfRecipeNotFound() {
        helper.initializeRecipeBook(3, recipeDao);
        Recipe recipe = recipeDao.getById(4);
        assertThat(recipe, is(nullValue()));
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
    public void rightRecipesAreLoadedWhenObjectInstantiated() {
        recipeDao.create(helper.createTestRecipe("Meatballs"));
        recipeDao.create(helper.createTestRecipe("Salmon soup"));
        recipeDao.create(helper.createTestRecipe("Pasta carbonara"));

        RecipeDao newRecipeDao = new FileRecipeDao(ingDaoMock, testRecipeFile.getAbsolutePath(), testRecipesIngsFile.getAbsolutePath());
        List<Recipe> recipes = newRecipeDao.getAll();
        List<String> recipeNames = recipes.stream().map(r -> r.getName()).collect(Collectors.toList());
        assertTrue(recipeNames.contains("Meatballs"));
        assertTrue(recipeNames.contains("Salmon soup"));
        assertTrue(recipeNames.contains("Pasta carbonara"));
    }

    @Test
    public void deleteRemovesRecipe() {
        Recipe meatballs = recipeDao.create(helper.createTestRecipe("Meatballs"));
        assertThat(recipeDao.getAll(), hasItem(meatballs));
        recipeDao.delete(meatballs);
        assertThat(recipeDao.getAll(), not(hasItem(meatballs)));
    }

}
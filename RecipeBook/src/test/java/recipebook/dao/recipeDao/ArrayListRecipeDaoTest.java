package recipebook.dao.recipeDao;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.dao.ingredientdao.ArrayListIngredientDao;
import recipebook.dao.recipedao.ArrayListRecipeDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.domain.recipe.Recipe;

public class ArrayListRecipeDaoTest {

    private RecipeDao recipeDao;
    private TestHelper helper;

    @Before
    public void setUp() {
        recipeDao = new ArrayListRecipeDao(new ArrayListIngredientDao());
        helper = new TestHelper();
        
    }

    @Test
    public void createdRecipeGetsRightId() {
        helper.initializeRecipeBook(3, recipeDao);
        Recipe recipe = recipeDao.create(helper.createTestRecipe("Salmon soup"));
        assertThat(recipe.getId(), is(4));
    }

    @Test
    public void recipeIsNotAddedIfItAlreadyExists() {
        Recipe recipe = helper.createTestRecipe("Salmon soup");
        recipeDao.create(recipe);
        assertThat(recipeDao.getAll().size(), is(1));
        recipeDao.create(recipe);
        assertThat(recipeDao.getAll().size(), is(1));
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

}

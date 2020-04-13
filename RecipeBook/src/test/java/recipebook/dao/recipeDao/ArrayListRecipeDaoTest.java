package recipebook.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.domain.Recipe;

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

   
}

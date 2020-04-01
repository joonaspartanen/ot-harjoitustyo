package recipebook.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.domain.Ingredient;
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
        initializeRecipeBook(3);
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

    public void initializeRecipeBook(int numberOfRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();

        for (int i = 0; i < numberOfRecipes; i++) {
            recipes.add(new Recipe(i, "Recipe " + i, ingredients, 40, "Cook until done"));
        }
        recipeDao.setRecipes(recipes);
    }
}

package recipebook.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

public class ArrayListRecipeDaoTest {

    private RecipeDao recipeDao;

    @Before
    public void setUp() {
        recipeDao = new ArrayListRecipeDao();
    }

    @Test
    public void createdRecipeGetsRightId() {
        initializeRecipeBook(3);
        Recipe recipe = recipeDao.create(createTestRecipe("Salmon soup"));
        assertThat(recipe.getId(), is(4));
    }

    public void initializeRecipeBook(int numberOfRecipes) {
        List<Recipe> recipes = new ArrayList<>();
        Map<Ingredient, Integer> ingredients = createTestIngredientList();

        for (int i = 0; i < numberOfRecipes; i++) {
            recipes.add(new Recipe(i, "Recipe " + i, ingredients, 40, "Cook until done"));
        }
        recipeDao.setRecipes(recipes);
    }

    public Recipe createTestRecipe(String name) {
        Map<Ingredient, Integer> ingredients = createTestIngredientList();
        return new Recipe(name, ingredients, 30, "Cook until done");
    }

    public Map<Ingredient, Integer> createTestIngredientList() {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(new Ingredient("Chicken"), 300);
        ingredients.put(new Ingredient("Butter"), 40);
        return ingredients;
    }
}

package recipebook.domain.recipe;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.dao.ingredientdao.ArrayListIngredientDao;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.ArrayListRecipeDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.domain.ingredient.Ingredient;

public class RecipeServiceTest {

    private RecipeService recipeService;
    private IngredientDao ingDaoMock;
    private RecipeDao recipeDaoMock;
    private TestHelper helper;

    @Before
    public void setUp() {
        ingDaoMock = new ArrayListIngredientDao();
        recipeDaoMock = new ArrayListRecipeDao(ingDaoMock);
        recipeService = new RecipeService(recipeDaoMock);
        helper = new TestHelper();
    }

    @Test
    public void createRecipeReturnsRecipeWithRightProperties() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        Recipe recipe = recipeService.createRecipe("Chicken soup", ingredients, 20, "Cook until done.");
        assertThat(recipe.getName(), is(equalTo("Chicken soup")));
        assertThat(recipe.getIngredients(), is(equalTo(ingredients)));
        assertThat(recipe.getTime(), is(20));
        assertThat(recipe.getInstructions(), is(equalTo("Cook until done.")));
    }

    @Test
    public void findByIngredientReturnsRightRecipes() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientListWithNames("chicken", "onion", "garlic",
                "cream", "lemon");
        Recipe lemonChicken = recipeService.createRecipe("Lemon chicken", ingredients, 10, "Cook.");
        ingredients = helper.createTestIngredientListWithNames("salmon", "butter", "lemon");
        Recipe roastedSalmon = recipeService.createRecipe("Roasted salmon", ingredients, 10, "Cook");
        ingredients = helper.createTestIngredientListWithNames("chicken", "potato", "corn", "carrot");
        Recipe chickenSoup = recipeService.createRecipe("Chicken soup", ingredients, 10, "Cook");
        List<Recipe> foundRecipes = recipeService.findByIngredient("lemon");
        assertThat(foundRecipes, hasItem(lemonChicken));
        assertThat(foundRecipes, hasItem(roastedSalmon));
        assertThat(foundRecipes, not(hasItem(chickenSoup)));
    }

    @Test
    public void deleteRecipeByIdDeletesRecipeIfExistent() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        Recipe garlicChicken = recipeService.createRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertThat(recipeService.listAll(), hasItem(garlicChicken));
        int id = garlicChicken.getId();
        recipeService.deleteRecipeById(id);
        assertThat(recipeService.listAll(), not(hasItem(garlicChicken)));
    }

    @Test
    public void deleteRecipeByIdReturnsFalseIfRecipeNotFound() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        recipeService.createRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertFalse(recipeService.deleteRecipeById(5));
    }

}
package recipebook.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.dao.ArrayListIngredientDao;
import recipebook.dao.ArrayListRecipeDao;
import recipebook.dao.IngredientDao;
import recipebook.dao.RecipeDao;

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
    public void addRecipeReturnsRecipeWithRightProperties() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        Recipe recipe = recipeService.addRecipe("Chicken soup", ingredients, 20, "Cook until done.");
        assertThat(recipe.getName(), is(equalTo("Chicken soup")));
        assertThat(recipe.getIngredients(), is(equalTo(ingredients)));
        assertThat(recipe.getTime(), is(20));
        assertThat(recipe.getInstructions(), is(equalTo("Cook until done.")));
    }

    @Test
    public void findByIngredientReturnsRightRecipes() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientListWithNames("chicken", "onion", "garlic",
                "cream", "lemon");
        Recipe lemonChicken = recipeService.addRecipe("Lemon chicken", ingredients, 10, "Cook.");
        ingredients = helper.createTestIngredientListWithNames("salmon", "butter", "lemon");
        Recipe roastedSalmon = recipeService.addRecipe("Roasted salmon", ingredients, 10, "Cook");
        ingredients = helper.createTestIngredientListWithNames("chicken", "potato", "corn", "carrot");
        Recipe chickenSoup = recipeService.addRecipe("Chicken soup", ingredients, 10, "Cook");
        List<Recipe> foundRecipes = recipeService.findByIngredient("lemon");
        assertThat(foundRecipes, hasItem(lemonChicken));
        assertThat(foundRecipes, hasItem(roastedSalmon));
        assertThat(foundRecipes, not(hasItem(chickenSoup)));
    }

    @Test
    public void deleteRecipeByIdDeletesRecipeIfExistent() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        Recipe garlicChicken = recipeService.addRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertThat(recipeService.listAll(), hasItem(garlicChicken));
        int id = garlicChicken.getId();
        recipeService.deleteRecipeById(id);
        assertThat(recipeService.listAll(), not(hasItem(garlicChicken)));
    }

    @Test
    public void deleteRecipeByIdReturnsFalseIfRecipeNotFound() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        recipeService.addRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertFalse(recipeService.deleteRecipeById(5));
    }

}
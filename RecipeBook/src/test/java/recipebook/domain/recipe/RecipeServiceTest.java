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
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.user.BadUsernameException;
import recipebook.domain.user.UserDaoMock;
import recipebook.domain.user.UserService;

public class RecipeServiceTest {

    private RecipeService recipeService;
    private IngredientDao ingDaoMock;
    private RecipeDao recipeDaoMock;
    private UserDao userDaoMock;
    private UserService userService;
    private TestHelper helper;

    @Before
    public void setUp() {
        ingDaoMock = new ArrayListIngredientDao();
        recipeDaoMock = new ArrayListRecipeDao(ingDaoMock);
        userDaoMock = new UserDaoMock();
        userService = new UserService(userDaoMock);
        recipeService = new RecipeService(recipeDaoMock, userService);
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
    public void deleteRecipeByIdDeletesExistingRecipeIfCreatedByCurrentUser()
            throws UserNotFoundException, BadUsernameException {
        userService.createUser("Tester");
        userService.login("Tester");
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        Recipe garlicChicken = recipeService.createRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertThat(recipeService.listAll(), hasItem(garlicChicken));
        int id = garlicChicken.getId();

        recipeService.deleteRecipeById(id);

        assertThat(recipeService.listAll(), not(hasItem(garlicChicken)));
    }

    @Test
    public void deleteRecipeByIdWontDeleteExistingRecipeIfNotCreatedByCurrentUser()
            throws UserNotFoundException, BadUsernameException {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        userService.createUser("Tester 1");
        userService.login("Tester 1");
        Recipe garlicChicken = recipeService.createRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertThat(recipeService.listAll(), hasItem(garlicChicken));
        userService.logout();
        userService.createUser("Tester 2");
        userService.login("Tester 2");
        int id = garlicChicken.getId();

        recipeService.deleteRecipeById(id);

        assertThat(recipeService.listAll(), hasItem(garlicChicken));
    }

    @Test
    public void deleteRecipeByIdReturnsFalseIfRecipeNotFound() {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        recipeService.createRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertFalse(recipeService.deleteRecipeById(5));
    }

}
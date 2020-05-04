package recipebook.domain.recipe;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.dao.DataStoreException;
import recipebook.dao.ingredientdao.IngredientDaoMock;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDaoMock;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.*;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.ingredient.IngredientService;
import recipebook.domain.user.BadUsernameException;
import recipebook.domain.user.UserService;

public class RecipeServiceTest {

    private RecipeService recipeService;
    private IngredientDao ingredientDaoMock;
    private RecipeDao recipeDaoMock;
    private UserDao userDaoMock;
    private UserService userService;
    private IngredientService ingredientService;
    private TestHelper helper;

    @Before
    public void setUp() throws DataStoreException {
        ingredientDaoMock = new IngredientDaoMock();
        userDaoMock = new UserDaoMock();
        recipeDaoMock = new RecipeDaoMock(ingredientDaoMock, userDaoMock);
        userService = new UserService(userDaoMock);
        ingredientService = new IngredientService(ingredientDaoMock);
        recipeService = new RecipeService(recipeDaoMock, userService, ingredientService);
        helper = new TestHelper(ingredientDaoMock, userDaoMock);
    }

    @Test
    public void createRecipeReturnsRecipeWithRightProperties() throws DataStoreException {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();

        Recipe recipe = recipeService.createRecipe("Chicken soup", ingredients, 20, "Cook until done.");

        assertThat(recipe.getName(), is(equalTo("Chicken soup")));
        assertThat(recipe.getIngredients(), is(equalTo(ingredients)));
        assertThat(recipe.getTime(), is(20));
        assertThat(recipe.getInstructions(), is(equalTo("Cook until done.")));
    }

    @Test
    public void findByNameReturnRightRecipes() throws DataStoreException, UserNotFoundException {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        Recipe tofuBowl = recipeService.createRecipe("Tofu bowl", ingredients, 10, "Mix.");
        Recipe salmonSoup = recipeService.createRecipe("Salmon soup", ingredients, 20, "Cook.");
        Recipe tofuWok = recipeService.createRecipe("Tofu wok", ingredients, 10, "Cook well.");

        List<Recipe> recipes = recipeService.findByName("Tofu");

        assertThat(recipes.size(), is(2));
        assertThat(recipes, hasItem(tofuBowl));
        assertThat(recipes, hasItem(tofuWok));
        assertThat(recipes, not(hasItem(salmonSoup)));
    }

    @Test
    public void findByIngredientReturnsRightRecipes() throws DataStoreException, UserNotFoundException {
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
            throws UserNotFoundException, BadUsernameException, DataStoreException {
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
            throws UserNotFoundException, BadUsernameException, DataStoreException {
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
    public void deleteRecipeByIdReturnsFalseIfRecipeNotFound() throws DataStoreException, UserNotFoundException {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientList();
        recipeService.createRecipe("Garlic chicken", ingredients, 10, "Cook");
        assertFalse(recipeService.deleteRecipeById(5));
    }
}
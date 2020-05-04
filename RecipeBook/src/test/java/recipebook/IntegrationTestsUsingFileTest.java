package recipebook;

import java.util.List;
import java.util.Map;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.*;
import org.junit.rules.TemporaryFolder;
import recipebook.dao.*;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.recipe.RecipeService;
import recipebook.domain.user.BadUsernameException;
import recipebook.domain.user.UserService;

public class IntegrationTestsUsingFileTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private DataStoreConnector connector;
    private UserService userService;
    private RecipeService recipeService;
    private TestHelper helper;
    private RecipeDao recipeDao;

    @Before
    public void setUp() throws DataStoreException, UserNotFoundException {
        connector = new FileConnector(testFolder.getRoot().toString() + "/");
        connector.initializeDataStore();
        UserDao userDao = connector.getUserDao();
        IngredientDao ingredientDao = connector.getIngredientDao();
        recipeDao = connector.getRecipeDao();
        userService = new UserService(userDao);
        recipeService = new RecipeService(recipeDao, userService);
        helper = new TestHelper(ingredientDao, userDao);
    }

    @Test
    public void userCanAddRecipes() throws BadUsernameException, UserNotFoundException, DataStoreException {
        createUserAndLogin("Integration tester");
        Recipe salmonSoup = addTestRecipeWithName("Salmon soup");

        List<Recipe> allRecipes = recipeService.listAll();
        assertThat(allRecipes, hasItem(salmonSoup));

        Recipe foundRecipe = allRecipes.stream().filter(r -> r.getName().equals("Salmon soup")).findFirst()
                .orElse(null);
        int userId = userService.getCurrentUser().getId();
        assertThat(foundRecipe.getAuthorId(), is(userId));
    }

    @Test
    public void onlyRecipeAuthorCanDeleteRecipe()
            throws BadUsernameException, UserNotFoundException, DataStoreException {
        createUserAndLogin("Tester 1");
        Recipe salmonSoup = addTestRecipeWithName("Salmon soup");
        userService.logout();

        createUserAndLogin("Tester 2");
        recipeService.deleteRecipeById(salmonSoup.getId());
        List<Recipe> allRecipes = recipeService.listAll();
        assertThat(allRecipes, hasItem(salmonSoup));
        userService.logout();

        userService.login("Tester 1");
        recipeService.deleteRecipeById(salmonSoup.getId());
        allRecipes = recipeService.listAll();

        assertThat(allRecipes, not(hasItem(salmonSoup)));
    }

    @Test
    public void userCanAddRecipesToFavorites() throws BadUsernameException, UserNotFoundException, DataStoreException {
        createUserAndLogin("Tester 1");
        Recipe salmonSoup = addTestRecipeWithName("Salmon soup");
        userService.logout();

        createUserAndLogin("Tester 2");
        Recipe chickenSoup = addTestRecipeWithName("Chicken soup");
        recipeService.addRecipeToFavorites(salmonSoup);

        List<Recipe> favoriteRecipes = recipeService.getFavoriteRecipes();

        assertThat(favoriteRecipes, hasItem(salmonSoup));
        assertThat(favoriteRecipes, hasItem(chickenSoup));
    }

    private void createUserAndLogin(String username)
            throws BadUsernameException, UserNotFoundException, DataStoreException {
        userService.createUser(username);
        userService.login(username);
    }

    private Recipe addTestRecipeWithName(String recipeName) throws DataStoreException {
        Map<Ingredient, Integer> ingredients = helper.createTestIngredientListWithNames("salmon", "milk", "butter",
                "potato");
        return recipeService.createRecipe(recipeName, ingredients, 40, "Boil until done.");
    }

}

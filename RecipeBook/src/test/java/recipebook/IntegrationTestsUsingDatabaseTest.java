package recipebook;

import java.util.List;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import static org.junit.Assert.*;
import org.junit.rules.TemporaryFolder;
import recipebook.dao.*;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.IngredientService;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.recipe.RecipeService;
import recipebook.domain.user.BadUsernameException;
import recipebook.domain.user.UserService;

public class IntegrationTestsUsingDatabaseTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private DataStoreConnector connector;
    private UserService userService;
    private IngredientService ingredientService;
    private RecipeService recipeService;
    private TestHelper helper;
    private RecipeDao recipeDao;

    @Before
    public void setUp() throws DataStoreException, UserNotFoundException {
        connector = new DatabaseConnector(testFolder.getRoot().toString() + "/");
        connector.initializeDataStore();
        UserDao userDao = connector.getUserDao();
        IngredientDao ingredientDao = connector.getIngredientDao();
        recipeDao = connector.getRecipeDao();
        userService = new UserService(userDao);
        ingredientService = new IngredientService(ingredientDao);
        recipeService = new RecipeService(recipeDao, userService, ingredientService);
        helper = new TestHelper(ingredientDao, userDao);
    }

    @Test
    public void userCanAddRecipes() throws BadUsernameException, UserNotFoundException, DataStoreException {
        helper.createUserAndLogin(userService, "Integration tester");
        Recipe salmonSoup = helper.addTestRecipeWithName(recipeService, "Salmon soup");

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
        helper.createUserAndLogin(userService, "Tester 1");
        Recipe salmonSoup = helper.addTestRecipeWithName(recipeService, "Salmon soup");
        userService.logout();

        helper.createUserAndLogin(userService, "Tester 2");
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
        helper.createUserAndLogin(userService, "Tester 1");
        Recipe salmonSoup = helper.addTestRecipeWithName(recipeService, "Salmon soup");
        userService.logout();

        helper.createUserAndLogin(userService, "Tester 2");
        Recipe chickenSoup = helper.addTestRecipeWithName(recipeService, "Chicken soup");
        recipeService.addRecipeToFavorites(salmonSoup);

        List<Recipe> favoriteRecipes = recipeService.getFavoriteRecipes();

        assertThat(favoriteRecipes, hasItem(salmonSoup));
        assertThat(favoriteRecipes, hasItem(chickenSoup));
    }
}

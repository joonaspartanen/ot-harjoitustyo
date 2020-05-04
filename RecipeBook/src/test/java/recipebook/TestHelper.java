package recipebook;

import java.util.HashMap;
import java.util.Map;

import recipebook.dao.DataStoreException;
import recipebook.dao.ingredientdao.IngredientDaoMock;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserDao;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.recipe.RecipeService;
import recipebook.domain.user.BadUsernameException;
import recipebook.domain.user.User;
import recipebook.domain.user.UserService;
import recipebook.dao.userdao.UserDaoMock;
import recipebook.dao.userdao.UserNotFoundException;

public class TestHelper {

    private IngredientDao ingDao;
    private User testUser;

    public TestHelper(IngredientDao ingDao, UserDao userDao) throws DataStoreException {
        this.ingDao = ingDao;
        testUser = userDao.create(new User("Test user"));
    }

    public TestHelper() throws DataStoreException {
        this(new IngredientDaoMock(), new UserDaoMock());
    }

    public Map<Ingredient, Integer> createTestIngredientList() throws DataStoreException {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        Ingredient ingredient1 = ingDao.create(new Ingredient("ingredient1", "g"));
        Ingredient ingredient2 = ingDao.create(new Ingredient("ingredient2", "g"));

        ingredients.put(ingredient1, 300);
        ingredients.put(ingredient2, 40);
        return ingredients;
    }

    public Map<Ingredient, Integer> createTestIngredientListWithNames(String... ingredientNames)
            throws DataStoreException {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        for (String name : ingredientNames) {
            Ingredient ingredient = createIngredientIfNotExists(name);
            ingredients.put(ingredient, 300);
        }
        return ingredients;
    }

    private Ingredient createIngredientIfNotExists(String name) throws DataStoreException {
        Ingredient ingredient = ingDao.getByNameAndUnit(name, "g");
        if (ingredient == null) {
            ingredient = ingDao.create(new Ingredient(name, "g"));
        }
        return ingredient;
    }

    public Recipe createTestRecipe(String name) throws DataStoreException {
        Map<Ingredient, Integer> ingredients = createTestIngredientList();
        return new Recipe(name, ingredients, 30, "Cook until done", testUser);
    }

    public Recipe createTestRecipeWithIngredients(String recipeName, String... ingredientNames)
            throws DataStoreException {
        Map<Ingredient, Integer> ingredients = new HashMap<>();
        for (String name : ingredientNames) {
            Ingredient ingredient = createIngredientIfNotExists(name);
            ingredients.put(ingredient, 300);
        }
        Recipe recipe = new Recipe(recipeName, ingredients, 20, "Cook well", testUser);
        return recipe;
    }

    public void initializeRecipeBook(int numberOfRecipes, RecipeDao recipeDao) throws DataStoreException {
        Map<Ingredient, Integer> ingredients = createTestIngredientList();
        for (int i = 0; i < numberOfRecipes; i++) {
            recipeDao.create(new Recipe(i, "Recipe " + i, ingredients, 40, "Cook until done", testUser));
        }
    }

    public User getTestUser() {
        return testUser;
    }

    public void createUserAndLogin(UserService userService, String username)
            throws BadUsernameException, UserNotFoundException, DataStoreException {
        userService.createUser(username);
        userService.login(username);
    }

    public Recipe addTestRecipeWithName(RecipeService recipeService, String recipeName) throws DataStoreException {
        Map<Ingredient, Integer> ingredients = createTestIngredientListWithNames("salmon", "milk", "butter", "potato");
        return recipeService.createRecipe(recipeName, ingredients, 40, "Boil until done.");
    }

}

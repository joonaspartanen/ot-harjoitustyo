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
import recipebook.domain.user.User;
import recipebook.dao.userdao.UserDaoMock;

public class TestHelper {

    private final IngredientDao ingDao;
    private final User testUser;

    public TestHelper(final IngredientDao ingDao, final UserDao userDao) throws DataStoreException {
        this.ingDao = ingDao;
        testUser = userDao.create(new User("Test user"));
    }

    public TestHelper() throws DataStoreException {
        this(new IngredientDaoMock(), new UserDaoMock());
    }

    public Map<Ingredient, Integer> createTestIngredientList() throws DataStoreException {
        final Map<Ingredient, Integer> ingredients = new HashMap<>();
        final Ingredient ingredient1 = ingDao.create(new Ingredient("ingredient1", "g"));
        final Ingredient ingredient2 = ingDao.create(new Ingredient("ingredient2", "g"));

        ingredients.put(ingredient1, 300);
        ingredients.put(ingredient2, 40);
        return ingredients;
    }

    public Map<Ingredient, Integer> createTestIngredientListWithNames(final String... ingredientNames)
            throws DataStoreException {
        final Map<Ingredient, Integer> ingredients = new HashMap<>();
        for (final String name : ingredientNames) {
            final Ingredient ingredient = ingDao.create(new Ingredient(name, "g"));
            ingredients.put(ingredient, 300);
        }
        return ingredients;
    }

    public Recipe createTestRecipe(final String name) throws DataStoreException {
        final Map<Ingredient, Integer> ingredients = createTestIngredientList();
        return new Recipe(name, ingredients, 30, "Cook until done", testUser);
    }

    public Recipe createTestRecipeWithIngredients(final String recipeName, final String... ingredientNames)
            throws DataStoreException {
        final Map<Ingredient, Integer> ingredients = new HashMap<>();
        for (final String name : ingredientNames) {
            final Ingredient ingredient = ingDao.create(new Ingredient(name, "g"));
            ingredients.put(ingredient, 300);
        }
        final Recipe recipe = new Recipe(recipeName, ingredients, 20, "Cook well", testUser);
        return recipe;
    }

    public void initializeRecipeBook(final int numberOfRecipes, final RecipeDao recipeDao) throws DataStoreException {
        final Map<Ingredient, Integer> ingredients = createTestIngredientList();
        for (int i = 0; i < numberOfRecipes; i++) {
            recipeDao.create(new Recipe(i, "Recipe " + i, ingredients, 40, "Cook until done", testUser));
        }
    }

    public User getTestUser() {
        return testUser;
    }
}

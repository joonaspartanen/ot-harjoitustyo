package recipebook;

import java.util.HashMap;
import java.util.Map;

import recipebook.dao.ingredientdao.ArrayListIngredientDao;
import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.user.User;

public class TestHelper {

  private IngredientDao ingDao;
  private User testUser;

  public TestHelper(IngredientDao ingDao) {
    this.ingDao = ingDao;
    testUser = new User("Test user");
  }

  public TestHelper() {
    this.ingDao = new ArrayListIngredientDao();
    testUser = new User("Test user");
  }

  public Map<Ingredient, Integer> createTestIngredientList() {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    Ingredient ingredient1 = ingDao.create(new Ingredient("ingredient1", "g"));
    Ingredient ingredient2 = ingDao.create(new Ingredient("ingredient2", "g"));

    ingredients.put(ingredient1, 300);
    ingredients.put(ingredient2, 40);
    return ingredients;
  }

  public Map<Ingredient, Integer> createTestIngredientListWithNames(String... ingredientNames) {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    for (String name : ingredientNames) {
      Ingredient ingredient = ingDao.create(new Ingredient(name, "g"));
      ingredients.put(ingredient, 300);
    }
    return ingredients;
  }

  public Recipe createTestRecipe(String name) {
    Map<Ingredient, Integer> ingredients = createTestIngredientList();
    return new Recipe(name, ingredients, 30, "Cook until done", testUser);
  }

  public Recipe createTestRecipeWithIngredients(String recipeName, String... ingredientNames) {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    for (String name : ingredientNames) {
      Ingredient ingredient = ingDao.create(new Ingredient(name, "g"));
      ingredients.put(ingredient, 300);
    }
    Recipe recipe = new Recipe(recipeName, ingredients, 20, "Cook well", testUser);
    return recipe;
  }

  public void initializeRecipeBook(int numberOfRecipes, RecipeDao recipeDao) {
    Map<Ingredient, Integer> ingredients = createTestIngredientList();
    for (int i = 0; i < numberOfRecipes; i++) {
      recipeDao.create(new Recipe(i, "Recipe " + i, ingredients, 40, "Cook until done", testUser));
    }
  }

  public User getTestUser() {
    return testUser;
  }
}
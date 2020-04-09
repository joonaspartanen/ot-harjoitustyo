package recipebook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import recipebook.dao.RecipeDao;
import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

public class TestHelper {

  public Map<Ingredient, Integer> createTestIngredientList() {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    ingredients.put(new Ingredient("ingredient1"), 300);
    ingredients.put(new Ingredient("ingredient2"), 40);
    return ingredients;
  }

  public Map<Ingredient, Integer> createTestIngredientListWithNames(String... ingredientNames) {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    for (String name : ingredientNames) {
      ingredients.put(new Ingredient(name), 300);
    }
    return ingredients;
  }

  public Recipe createTestRecipe(String name) {
    Map<Ingredient, Integer> ingredients = createTestIngredientList();
    return new Recipe(name, ingredients, 30, "Cook until done");
  }

  public Recipe createTestRecipeWithIngredients(String recipeName, String... ingredientNames) {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    for (String name : ingredientNames) {
      ingredients.put(new Ingredient(name), 100);
    }
    Recipe recipe = new Recipe(recipeName, ingredients, 20, "Cook well");
    return recipe;
  }

  public void initializeRecipeBook(int numberOfRecipes, RecipeDao recipeDao) {
    List<Recipe> recipes = new ArrayList<>();
    Map<Ingredient, Integer> ingredients = createTestIngredientList();

    for (int i = 0; i < numberOfRecipes; i++) {
      recipes.add(new Recipe(i, "Recipe " + i, ingredients, 40, "Cook until done"));
    }
    recipeDao.setRecipes(recipes);
  }

}
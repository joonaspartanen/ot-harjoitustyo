package recipebook;

import java.util.HashMap;
import java.util.Map;

import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

public class TestHelper {

  public Map<Ingredient, Integer> createTestIngredientList() {
    Map<Ingredient, Integer> ingredients = new HashMap<>();
    ingredients.put(new Ingredient("Chicken"), 300);
    ingredients.put(new Ingredient("Butter"), 40);
    return ingredients;
  }

  public Recipe createTestRecipe(String name) {
    Map<Ingredient, Integer> ingredients = createTestIngredientList();
    return new Recipe(name, ingredients, 30, "Cook until done");
  }

}
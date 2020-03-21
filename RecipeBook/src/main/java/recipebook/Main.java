package recipebook;

import java.util.HashMap;
import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

public class Main {

    public static void main(String[] args) {
        System.out.println("Starting app");
        System.out.println("");
        
        Ingredient chicken = new Ingredient("Chicken");
        Ingredient butter = new Ingredient("Butter");
        HashMap<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(chicken, 400);
        ingredients.put(butter, 50);
        
        Recipe testRecipe = new Recipe(1, "Butter Chicken", ingredients, 40, "Cook until ready");
        
        System.out.println(testRecipe);
        
    }
}

package recipebook.domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.HashMap;

public class RecipeTest {

    Recipe butterChicken;

    @Before
    public void setUp() {
        Ingredient chicken = new Ingredient("Chicken");
        Ingredient butter = new Ingredient("Butter");
        Ingredient onion = new Ingredient("Onion");
        HashMap<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(chicken, 400);
        ingredients.put(butter, 50);
        ingredients.put(onion, 50);
        butterChicken = new Recipe(1, "Butter Chicken", ingredients, 40, "Cook until ready");
    }

    @Test
    public void toStringFormatsRecipeProperly() {
        String result = butterChicken.toString();
        assertEquals("Butter Chicken\n\nIngredients:\nButter: 50 g\nChicken: 400 g\nOnion: 50 g\n\nCooking time: 40 min\n\nInstructions:\nCook until ready\n", result);
    }

}

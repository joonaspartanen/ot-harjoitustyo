package recipebook.domain.recipe;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.user.User;

public class RecipeTest {

    Recipe butterChicken;
    HashMap<Ingredient, Integer> ingredients;
    TestHelper helper;

    @Before
    public void setUp() {
        helper = new TestHelper();
        Ingredient chicken = new Ingredient("chicken");
        Ingredient butter = new Ingredient("butter");
        Ingredient onion = new Ingredient("onion");
        ingredients = new HashMap<>();
        ingredients.put(chicken, 400);
        ingredients.put(butter, 50);
        ingredients.put(onion, 50);
        User testUser = new User("tester");
        butterChicken = new Recipe(1, "Butter Chicken", ingredients, 40, "Cook until ready", testUser);
    }

    @Test
    public void toStringFormatsRecipeProperly() {
        String result = butterChicken.toString();
        assertEquals(
                "Butter Chicken\n\nIngredients:\n50 g butter\n400 g chicken\n50 g onion\n\nCooking time: 40 min\n\nInstructions:\nCook until ready\n\nRecipe created by: tester\n",
                result);
    }

    @Test
    public void equalsReturnsTrueIfRecipesEqual() {
        Recipe otherRecipe = new Recipe(1, "Butter Chicken", ingredients, 40, "Cook until ready");
        assertThat(butterChicken, is(equalTo(otherRecipe)));
    }

    @Test
    public void equalsReturnsFalseIfRecipeIdsDiffer() {
        Recipe otherRecipe = new Recipe(2, "Butter Chicken", ingredients, 40, "Cook until ready");
        assertThat(butterChicken, is(not(equalTo(otherRecipe))));
    }

    @Test
    public void equalsReturnsFalseIfRecipeNamesDiffer() {
        Recipe otherRecipe = new Recipe(1, "Better Chicken", ingredients, 40, "Cook until ready");
        assertThat(butterChicken, is(not(equalTo(otherRecipe))));
    }

    @Test
    public void equalsReturnsFalseIfRecipeTimesDiffer() {
        Recipe otherRecipe = new Recipe(1, "Butter Chicken", ingredients, 120, "Cook until ready");
        assertThat(butterChicken, is(not(equalTo(otherRecipe))));
    }

    @Test
    public void equalsReturnsFalseIfRecipeInstructionsDiffer() {
        Recipe otherRecipe = new Recipe(1, "Butter Chicken", ingredients, 40, "Cook until well done");
        assertThat(butterChicken, is(not(equalTo(otherRecipe))));
    }

    @Test
    public void equalsReturnsFalseIfRecipeIngredientsDiffer() {
        Map<Ingredient, Integer> otherIngredients = helper.createTestIngredientListWithNames("chicken", "butter",
                "cream");
        Recipe otherRecipe = new Recipe(1, "Butter Chicken", otherIngredients, 40, "Cook until ready");
        assertThat(butterChicken, is(not(equalTo(otherRecipe))));
    }

    @Test
    public void equalsReturnsFalseIfComparedWithOtherClass() {
        Ingredient chicken = new Ingredient("chicken", "g");
        assertThat(butterChicken, is(not(equalTo(chicken))));
    }

}

package recipebook.domain.ingredient;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import recipebook.TestHelper;
import recipebook.domain.recipe.Recipe;

public class IngredientTest {

    TestHelper helper;

    @Before
    public void setUp() {
        helper = new TestHelper();
    }

    @Test
    public void equalsReturnsTrueIfComparedWithItself() {
        Ingredient firstChicken = new Ingredient("chicken", "g");
        assertThat(firstChicken, is(equalTo(firstChicken)));
    }

    @Test
    public void equalsReturnsTrueIfIngredientsEqual() {
        Ingredient firstChicken = new Ingredient("chicken", "g");
        Ingredient otherChicken = new Ingredient("chicken", "g");
        assertThat(firstChicken, is(equalTo(otherChicken)));
    }

    @Test
    public void equalsReturnsFalseIfIngredientIdsDiffer() {
        Ingredient firstChicken = new Ingredient(1, "chicken", "g");
        Ingredient otherChicken = new Ingredient(2, "chicken", "g");
        assertThat(firstChicken, is(not(equalTo(otherChicken))));
    }

    @Test
    public void equalsReturnsFalseIfIngredientNamesDiffer() {
        Ingredient firstChicken = new Ingredient(1, "chicken", "g");
        Ingredient otherChicken = new Ingredient(1, "chick", "g");
        assertThat(firstChicken, is(not(equalTo(otherChicken))));
    }

    @Test
    public void equalsReturnsFalseIfIngredientUnitsDiffer() {
        Ingredient firstChicken = new Ingredient(1, "chicken", "g");
        Ingredient otherChicken = new Ingredient(1, "chicken", "kg");
        assertThat(firstChicken, is(not(equalTo(otherChicken))));
    }

    @Test
    public void equalsReturnsFalseIfComparedWithOtherClass() {
        Ingredient firstChicken = new Ingredient(1, "chicken", "g");
        Recipe otherChicken = helper.createTestRecipe("chicken");
        assertThat(firstChicken, is(not(equalTo(otherChicken))));
    }
}
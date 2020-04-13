package recipebook.domain.ingredient;

import recipebook.domain.ingredient.IngredientService;
import recipebook.domain.ingredient.Ingredient;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import recipebook.dao.ingredientDao.ArrayListIngredientDao;

public class IngredientServiceTest {

    IngredientService ingService;

    @Before
    public void setUp() {
        ingService = new IngredientService(new ArrayListIngredientDao());
    }

    @Test
    public void addedIngredientHasRightProperties() {
        Ingredient ingredient = ingService.addIngredient("chicken", "g");
        assertThat(ingredient.getName(), is(equalTo("chicken")));
        assertThat(ingredient.getUnit(), is(equalTo("g")));
    }

    @Test
    public void unitDefaultsToGramsIfNotSpecified() {
        Ingredient ingredient = ingService.addIngredient("chicken");
        assertThat(ingredient.getName(), is(equalTo("chicken")));
        assertThat(ingredient.getUnit(), is(equalTo("g")));
    }

    @Test
    public void addIngredientReturnsExistingIngredientIfIngredientAlreadyExists() {
        Ingredient ingredient = ingService.addIngredient("tomato");
        assertThat(ingredient.getId(), is(1));
        ingredient = ingService.addIngredient("tomato");
        assertThat(ingredient.getId(), is(1));
    }

    @Test
    public void listAllReturnsRightNumberOfIngredients() {
        ingService.addIngredient("chicken");
        ingService.addIngredient("garlic");
        ingService.addIngredient("milk");
        assertThat(ingService.listAll().size(), is(equalTo(3)));
    }

    @Test
    public void findByNameReturnsRightIngredientIfFound() {
        ingService.addIngredient("chicken");
        List<Ingredient> ingredients = ingService.findByName("chicken");
        assertThat(ingredients.get(0).getName(), is(equalTo("chicken")));
    }

    @Test
    public void findByNameReturnsEmptyListIfIngredientNotFound() {
        ingService.addIngredient("chicken");
        List<Ingredient> ingredients = ingService.findByName("salmon");
        assertTrue(ingredients.isEmpty());
    }

    @Test
    public void findByNameAndUnitReturnsRightIngredientIfFound() {
        ingService.addIngredient("chicken", "g");
        ingService.addIngredient("chicken", "kg");
        Ingredient foundIngredient = ingService.findByNameAndUnit("chicken", "kg");
        assertThat(foundIngredient.getName(), is(equalTo("chicken")));
        assertThat(foundIngredient.getUnit(), is(equalTo("kg")));
    }
}

package recipebook.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import recipebook.dao.ArrayListIngredientDao;

public class IngredientServiceTest {

    IngredientService ingService;

    @Before
    public void setUp() {
        ingService = new IngredientService(new ArrayListIngredientDao());
    }

    @Test
    public void addsIngredientProperly() {
        Ingredient ingredient = ingService.addIngredient("Chicken", "g");
        assertThat(ingredient.getName(), is(equalTo("Chicken")));
        assertThat(ingredient.getUnit(), is(equalTo("g")));
    }

    @Test
    public void unitDefaultsToGramsIfNotSpecified() {
        Ingredient ingredient = ingService.addIngredient("Chicken");
        assertThat(ingredient.getName(), is(equalTo("Chicken")));
        assertThat(ingredient.getUnit(), is(equalTo("g")));
    }

    @Test
    public void listAllReturnsRightNumberOfIngredients() {
        ingService.addIngredient("Chicken");
        ingService.addIngredient("Garlic");
        ingService.addIngredient("Milk");
        assertThat(ingService.listAll().size(), is(equalTo(3)));
    }

    @Test
    public void findByNameReturnsRightIngredientIfFound() {
        ingService.addIngredient("Chicken");
        Ingredient ingredient = ingService.findByName("Chicken");
        assertThat(ingredient.getName(), is(equalTo("Chicken")));
    }

    @Test
    public void findByNameReturnsNullIfIngredientNotFound() {
        ingService.addIngredient("Chicken");
        Ingredient ingredient = ingService.findByName("Salmon");
        assertThat(ingredient, is(nullValue()));
    }
}

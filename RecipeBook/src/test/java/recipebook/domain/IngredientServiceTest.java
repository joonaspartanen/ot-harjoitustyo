package recipebook.domain;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

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
    public void listAllReturnsRightNumberOfIngredients() {
        ingService.addIngredient("chicken");
        ingService.addIngredient("garlic");
        ingService.addIngredient("milk");
        assertThat(ingService.listAll().size(), is(equalTo(3)));
    }

    @Test
    public void findByNameReturnsRightIngredientIfFound() {
        ingService.addIngredient("chicken");
        Ingredient ingredient = ingService.findByName("chicken");
        assertThat(ingredient.getName(), is(equalTo("chicken")));
    }

    @Test
    public void findByNameReturnsNullIfIngredientNotFound() {
        ingService.addIngredient("chicken");
        Ingredient ingredient = ingService.findByName("salmon");
        assertThat(ingredient, is(nullValue()));
    }
}

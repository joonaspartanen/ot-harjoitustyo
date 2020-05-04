package recipebook.domain.ingredient;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import recipebook.dao.DataStoreException;
import recipebook.dao.ingredientdao.IngredientDaoMock;

public class IngredientServiceTest {

    IngredientService ingService;

    @Before
    public void setUp() {
        ingService = new IngredientService(new IngredientDaoMock());
    }

    @Test
    public void createIngredientReturnsIngredientWithRightProperties() throws DataStoreException {
        Ingredient ingredient = ingService.createIngredient("chicken", "g");
        assertThat(ingredient.getName(), is(equalTo("chicken")));
        assertThat(ingredient.getUnit(), is(equalTo("g")));
    }

    @Test
    public void unitDefaultsToGramsIfNotSpecified() throws DataStoreException {
        Ingredient ingredient = ingService.createIngredient("chicken");
        assertThat(ingredient.getName(), is(equalTo("chicken")));
        assertThat(ingredient.getUnit(), is(equalTo("g")));
    }

    @Test
    public void createIngredientReturnsExistingIngredientIfIngredientAlreadyExists() throws DataStoreException {
        Ingredient ingredient = ingService.createIngredient("tomato");
        assertThat(ingredient.getId(), is(1));
        ingredient = ingService.createIngredient("tomato");
        assertThat(ingredient.getId(), is(1));
    }

    @Test
    public void listAllReturnsRightNumberOfIngredients() throws DataStoreException {
        ingService.createIngredient("chicken");
        ingService.createIngredient("garlic");
        ingService.createIngredient("milk");
        assertThat(ingService.listAll().size(), is(equalTo(3)));
    }

    @Test
    public void findByNameReturnsRightIngredientIfFound() throws DataStoreException {
        ingService.createIngredient("chicken");
        List<Ingredient> ingredients = ingService.findByName("chicken");
        assertThat(ingredients.get(0).getName(), is(equalTo("chicken")));
    }

    @Test
    public void findByNameReturnsEmptyListIfIngredientNotFound() throws DataStoreException {
        ingService.createIngredient("chicken");
        List<Ingredient> ingredients = ingService.findByName("salmon");
        assertTrue(ingredients.isEmpty());
    }

    @Test
    public void findByNameAndUnitReturnsRightIngredientIfFound() throws DataStoreException {
        ingService.createIngredient("chicken", "g");
        ingService.createIngredient("chicken", "kg");
        Ingredient foundIngredient = ingService.findByNameAndUnit("chicken", "kg");
        assertThat(foundIngredient.getName(), is(equalTo("chicken")));
        assertThat(foundIngredient.getUnit(), is(equalTo("kg")));
    }
}

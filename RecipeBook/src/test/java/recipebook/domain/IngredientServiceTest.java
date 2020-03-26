package recipebook.domain;

import static org.junit.Assert.assertTrue;
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
        assertTrue(ingredient.getName().equals("Chicken"));
        assertTrue(ingredient.getUnit().equals("g"));
    }
}

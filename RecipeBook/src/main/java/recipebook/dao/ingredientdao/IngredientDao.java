package recipebook.dao.ingredientdao;

import java.util.List;

import recipebook.domain.ingredient.Ingredient;

/**
 * Dao interface for handling Ingredient-related data. 
 */
public interface IngredientDao {

    Ingredient create(Ingredient ingredient);

    List<Ingredient> getAll();

    List<Ingredient> getByName(String name);

    Ingredient getById(int id);

    Ingredient getByNameAndUnit(String name, String unit);

}

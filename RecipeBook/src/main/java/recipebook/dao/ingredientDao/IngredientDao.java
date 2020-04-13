package recipebook.dao.ingredientDao;

import java.util.List;

import recipebook.domain.ingredient.Ingredient;

public interface IngredientDao {

    Ingredient create(Ingredient ingredient);

    List<Ingredient> getAll();

    List<Ingredient> getByName(String name);

    Ingredient getById(int id);

    Ingredient getByNameAndUnit(String name, String unit);

}

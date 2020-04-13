package recipebook.dao;

import java.util.List;

import recipebook.domain.Ingredient;
import recipebook.domain.Recipe;

public interface IngredientDao {

    Ingredient create(Ingredient ingredient);

    List<Ingredient> getAll();

    Ingredient getByName(String name);

    Ingredient getById(int id);

	void createNewIngredients(Recipe recipe);
}

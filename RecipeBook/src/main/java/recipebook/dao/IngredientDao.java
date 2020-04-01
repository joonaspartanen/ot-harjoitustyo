package recipebook.dao;

import java.util.List;

import recipebook.domain.Ingredient;

public interface IngredientDao {

    Ingredient create(Ingredient ingredient);

    List<Ingredient> getAll();

    Ingredient getByName(String name);
}

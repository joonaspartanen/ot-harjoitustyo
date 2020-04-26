package recipebook.dao.recipedao;

import java.util.List;

import recipebook.domain.recipe.Recipe;

/**
 * Dao interface for handling Recipes-related data.
 */
public interface RecipeDao {

    Recipe create(Recipe recipe);

    List<Recipe> getAll();

    List<Recipe> getByName(String name);

    Recipe getById(int id);

    List<Recipe> getByIngredient(String name);

    void delete(Recipe recipe);
}

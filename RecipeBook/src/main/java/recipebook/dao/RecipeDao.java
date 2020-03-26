package recipebook.dao;

import java.util.List;
import recipebook.domain.Recipe;

public interface RecipeDao {

    Recipe create(Recipe recipe);

    List<Recipe> getAll();

    List<Recipe> getByName(String name);

    boolean delete(Recipe recipe);

    void setRecipes(List<Recipe> recipes);
}
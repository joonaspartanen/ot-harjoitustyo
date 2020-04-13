
package recipebook.domain.recipe;

import recipebook.domain.ingredient.Ingredient;
import java.util.List;
import java.util.Map;

import recipebook.dao.recipedao.RecipeDao;

public class RecipeService {

    private RecipeDao recipeDao;

    public RecipeService(RecipeDao recipeDao) {
        this.recipeDao = recipeDao;
    }

    public List<Recipe> listAll() {
        return recipeDao.getAll();
    }

    public Recipe addRecipe(String name, Map<Ingredient, Integer> ingredients, int time, String instructions) {
        Recipe recipe = new Recipe(name, ingredients, time, instructions);
        return recipeDao.create(recipe);
    }

    public List<Recipe> findByIngredient(String name) {
        return recipeDao.getByIngredient(name);
    }

    public boolean deleteRecipeById(int recipeId) {
        Recipe recipe = recipeDao.getById(recipeId);
        if (recipe == null) {
            return false;
        }
        recipeDao.delete(recipe);
        return true;
    }

}

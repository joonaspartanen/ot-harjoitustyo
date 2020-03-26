
package recipebook.domain;

import java.util.List;
import java.util.Map;
import recipebook.dao.RecipeDao;

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
    
    
}

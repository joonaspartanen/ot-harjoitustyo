package recipebook.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import recipebook.domain.Recipe;

// Preliminary implementation that stores recipes in ArrayList
public class ArrayListRecipeDao implements RecipeDao {

    List<Recipe> recipes;

    public ArrayListRecipeDao() {
        recipes = new ArrayList<>();
    }

    @Override
    public Recipe create(Recipe recipe) {
        if (!recipes.contains(recipe)) {
            recipe.setId(generateId());
            recipes.add(recipe);
        }
        return recipe;
    }

    @Override
    public List<Recipe> getAll() {
        return recipes;
    }

    @Override
    public List<Recipe> getByName(String name) {
        List<Recipe> foundRecipes = recipes.stream().filter(r -> r.getName().equals(name)).collect(Collectors.toList());
        return foundRecipes;
    }

    @Override
    public boolean delete(Recipe recipe) {
        if (recipes.contains(recipe)) {
            recipes.remove(recipe);
            return true;
        }
        return false;
    }

    public int generateId() {
        return recipes.size() + 1;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

}

package recipebook.dao.recipedao;

import recipebook.dao.ingredientdao.IngredientDao;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserDao;
import recipebook.domain.recipe.Recipe;

public class RecipeDaoMock implements RecipeDao {

    List<Recipe> recipes;
    IngredientDao ingDao;
    Map<Integer, List<Recipe>> favoriteRecipes;

    public RecipeDaoMock(IngredientDao ingDao, UserDao userDao) {
        recipes = new ArrayList<>();
        this.ingDao = ingDao;
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
        List<Recipe> foundRecipes = recipes.stream().filter(r -> r.getName().contains(name))
                .collect(Collectors.toList());
        return foundRecipes;
    }

    @Override
    public Recipe getById(int id) {
        return recipes.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Recipe> getByIngredient(String ingredientName) {
        List<Recipe> foundRecipes = new ArrayList<>();
        for (Recipe recipe : recipes) {
            if (recipeContainsIngredient(recipe, ingredientName)) {
                foundRecipes.add(recipe);
            }
        }
        return foundRecipes;
    }

    private boolean recipeContainsIngredient(Recipe recipe, String ingredientName) {
        List<String> ingredientNames = recipe.getIngredients().keySet().stream().map(i -> i.getName())
                .collect(Collectors.toList());
        return ingredientNames.contains(ingredientName.toLowerCase());
    }

    @Override
    public void delete(Recipe recipe) {
        recipes.remove(recipe);
    }

    private int generateId() {
        return recipes.size() + 1;
    }

    @Override
    public void saveRecipeToFavorites(int userId, Recipe recipe) {
        favoriteRecipes.putIfAbsent(userId, new ArrayList<>());
        favoriteRecipes.get(userId).add(recipe);
    }

    @Override
    public List<Recipe> getFavoriteRecipes(int userId) {
        return favoriteRecipes.get(userId);
    }

}

package recipebook.domain.recipe;

import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.user.User;
import recipebook.domain.user.UserService;

import java.util.List;
import java.util.Map;

import recipebook.dao.DataStoreException;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserNotFoundException;

/**
 * Contains the business logic for handling Recipes objects. Dependencies are
 * injected in the constructor.
 */
public class RecipeService {

    private RecipeDao recipeDao;
    private UserService userService;

    public RecipeService(RecipeDao recipeDao, UserService userService) {
        this.recipeDao = recipeDao;
        this.userService = userService;
    }

    /**
     * Method to fetch all recipes stored in the current data store.
     *
     * @return List of recipes.
     * @throws DataStoreException
     */
    public List<Recipe> listAll() throws DataStoreException, UserNotFoundException {
        return recipeDao.getAll();
    }

    /**
     * Method to create a new recipe and store using the DAO dependency. Parameters
     * self-explanatory. Fetches the recipe author from the UserService.
     *
     * @param name
     * @param ingredients
     * @param time
     * @param instructions
     * @return The newly created Recipe object.
     */
    public Recipe createRecipe(String name, Map<Ingredient, Integer> ingredients, int time, String instructions)
            throws DataStoreException {
        User currentUser = userService.getCurrentUser();
        Recipe recipe = new Recipe(name, ingredients, time, instructions, currentUser);
        return recipeDao.create(recipe);
    }

    /**
     * Method to fetch all recipes that contain certain ingredient.
     *
     * @param name Name of the ingredient that matching recipes should contain.
     * @return List of recipes.
     * @throws DataStoreException
     */
    public List<Recipe> findByIngredient(String name) throws DataStoreException, UserNotFoundException {
        
        return recipeDao.getByIngredient(name);
    }

    /**
     * Method to delete a recipe from the database or file. Checks if the recipe
     * exists and if the current user is its author.
     *
     * @param recipeId Id of the recipe to delete.
     * @return true if the recipe is deleted successfully; false if the operation
     *         fails or is denied.
     * @throws DataStoreException
     */
    public boolean deleteRecipeById(int recipeId) throws DataStoreException, UserNotFoundException {
        Recipe recipe = recipeDao.getById(recipeId);
        if (recipe == null) {
            return false;
        }

        User currentUser = userService.getCurrentUser();

        if (userAllowedToDeleteRecipe(currentUser, recipe)) {
            recipeDao.delete(recipe);
            return true;
        }

        return false;
    }

    /**
     * Adds a recipe to the favorite recipes of the current user.
     * 
     * @param recipe The recipe to be added to favorites.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    public void addRecipeToFavorites(Recipe recipe) throws DataStoreException, UserNotFoundException {
        User currentUser = userService.getCurrentUser();
        int userId = currentUser.getId();
        if (recipeNotInFavorites(recipe)) {
            recipeDao.saveRecipeToFavorites(userId, recipe);
        }
    }

    private boolean recipeNotInFavorites(Recipe recipe) throws DataStoreException, UserNotFoundException {
        return !getFavoriteRecipes().contains(recipe);
    }

    /**
     * Fetches the current user's favorite recipes.
     * 
     * @return List of recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    public List<Recipe> getFavoriteRecipes() throws DataStoreException, UserNotFoundException {
        User currentUser = userService.getCurrentUser();
        int userId = currentUser.getId();
        List<Recipe> favoriteRecipes = recipeDao.getFavoriteRecipes(userId);
        return favoriteRecipes;
    }

    private boolean userAllowedToDeleteRecipe(User user, Recipe recipe) {
        return user.getId() == recipe.getAuthorId();
    }

}

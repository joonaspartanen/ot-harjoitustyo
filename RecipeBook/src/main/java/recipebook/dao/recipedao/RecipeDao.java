package recipebook.dao.recipedao;

import java.util.List;

import recipebook.dao.DataStoreException;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;

/**
 * DAO interface for handling Recipes-related data.
 */
public interface RecipeDao {

    /**
     * Stores a recipe to the data store.
     *
     * @param recipe Recipe to be stored.
     * @return The stored recipe.
     * @throws DataStoreException
     */
    Recipe create(Recipe recipe) throws DataStoreException;

    /**
     * Fetches all recipes from the data store.
     *
     * @return List of recipes.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    List<Recipe> getAll() throws DataStoreException, UserNotFoundException;

    /**
     * Fetches recipes whose name contain the search term passed in as a parameter.
     *
     * @param name Recipe name used as a search term.
     * @return List of recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    List<Recipe> getByName(String name) throws DataStoreException, UserNotFoundException;

    /**
     * Fetches the recipes whose id matches the search term passed in as a
     * parameter. Single result expected.
     *
     * @param id The recipe id used as a search term.
     * @return The matching recipe.
     * @throws DataStoreException    if no results found.
     * @throws UserNotFoundException
     */
    Recipe getById(int id) throws DataStoreException, UserNotFoundException;

    /**
     * Fetches recipes that contain certain ingredient.
     *
     * @param ingredient The ingredient used as a search term.
     * @return List of recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    List<Recipe> getByIngredient(Ingredient ingredient) throws DataStoreException, UserNotFoundException;

    /**
     * Deletes a recipe from the data store.
     *
     * @param recipe The recipe to be deleted.
     * @throws DataStoreException
     */
    void delete(Recipe recipe) throws DataStoreException;

    /**
     * Add a recipe to user favorites.
     *
     * @param userId Id of the user who is adding the recipe to favorites.
     * @param recipe The recipe to be added to favorites.
     * @throws DataStoreException
     */
    void saveRecipeToFavorites(int userId, Recipe recipe) throws DataStoreException;

    /**
     * Fetches a list of user's favorite recipes.
     *
     * @param userId Id of the user whose favorite recipes are being fetched.
     * @return List of recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    List<Recipe> getFavoriteRecipes(int userId) throws DataStoreException, UserNotFoundException;
}

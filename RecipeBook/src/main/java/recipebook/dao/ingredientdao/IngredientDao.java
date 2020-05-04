package recipebook.dao.ingredientdao;

import java.util.List;

import recipebook.dao.DataStoreException;
import recipebook.domain.ingredient.Ingredient;

/**
 * DAO interface for handling ingredient-related data.
 */
public interface IngredientDao {

    /**
     * Stores an ingredient to the data store.
     *
     * @param ingredient Ingredient to be stored.
     * @return The stored ingredient.
     * @throws DataStoreException
     */
    Ingredient create(Ingredient ingredient) throws DataStoreException;

    /**
     * Fetches all ingredients from the data store.
     *
     * @return List of ingredients.
     * @throws DataStoreException
     */
    List<Ingredient> getAll() throws DataStoreException;

    /**
     * Fetches ingredients whose name matches the search term passed in as a
     * parameter.
     *
     * @param name The ingredient name used as a search term.
     * @return List of matching ingredients.
     * @throws DataStoreException
     */
    List<Ingredient> getByName(String name) throws DataStoreException;

    /**
     * Fetches the ingredient whose id matches the search term passed in as a
     * parameter. Single result expected.
     *
     * @param id The ingredient id used as a search term.
     * @return The matching ingredient.
     * @throws DataStoreException if no results found.
     */
    Ingredient getById(int id) throws DataStoreException;

    /**
     * Fetches the ingredient whose name and unit match the search terms passed in
     * as parameters. Single result expected.
     *
     * @param name The ingredient name used as a search term.
     * @param unit The ingredient unit used as a search term.
     * @return The matching ingredient.
     * @throws DataStoreException
     */
    Ingredient getByNameAndUnit(String name, String unit) throws DataStoreException;

}

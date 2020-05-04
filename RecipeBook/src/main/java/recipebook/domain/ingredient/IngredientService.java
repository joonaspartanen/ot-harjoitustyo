package recipebook.domain.ingredient;

import java.util.List;

import recipebook.dao.DataStoreException;
import recipebook.dao.ingredientdao.IngredientDao;

/**
 * Contains the business logic for handling Ingredient objects. The dao
 * dependency is injected in the constructor.
 */
public class IngredientService {

    private IngredientDao ingredientDao;

    public IngredientService(IngredientDao ingDao) {
        this.ingredientDao = ingDao;
    }

    /**
     * Method to fetch all ingredients stored in the current data store.
     * 
     * @return List of ingredients.
     * @throws DataStoreException
     */
    public List<Ingredient> listAll() throws DataStoreException {
        return ingredientDao.getAll();
    }

    /**
     * Method to create a new ingredient and store it by the dao dependency. Checks
     * if the ingredient with same name and unit exists to avoid duplicate items.
     * 
     * @param name Ingredient name.
     * @param unit Ingredient unit.
     * @return Returns the newly created Ingredient object or, if existent, an
     *         ingredient with the same name and unit.
     * @throws DataStoreException
     */
    public Ingredient createIngredient(String name, String unit) throws DataStoreException {
        Ingredient foundIngredient = ingredientDao.getByNameAndUnit(formatName(name), unit);
        if (foundIngredient == null) {
            return ingredientDao.create(new Ingredient(formatName(name), unit));
        }
        return foundIngredient;
    }

    /**
     * If the new ingredient's unit is not provided, it defaults to grams.
     * 
     * @see recipebook.domain.ingredient.IngredientService#createIngredient(String,
     *      String)
     * 
     * @param name Ingredient name.
     * @return Returns the newly created Ingredient object or, if existent, an
     *         ingredient with the same name and unit.
     * @throws DataStoreException
     */
    public Ingredient createIngredient(String name) throws DataStoreException {
        return createIngredient(name, "g");
    }

    /**
     * Method to fetch all ingredients whose name matches the parameter name. The
     * ingredient names are case-insensitive and the leading or trailing spaces are
     * trimmed.
     * 
     * @param name Ingredient name.
     * @return List of matching ingredients or an empty list if no matches are
     *         found.
     * @throws DataStoreException
     */
    public List<Ingredient> findByName(String name) throws DataStoreException {
        return ingredientDao.getByName(formatName(name));
    }

    /**
     * Method to fetch an ingredient with certain name and unit. Only one match is
     * expected as duplicates are not stored.
     * 
     * @param name Ingredient name.
     * @param unit Ingredient unit.
     * @return A matching Ingredient object or null if no matches are found.
     * @throws DataStoreException
     */
    public Ingredient findByNameAndUnit(String name, String unit) throws DataStoreException {
        return ingredientDao.getByNameAndUnit(formatName(name), unit);
    }

    private String formatName(String name) {
        return name.toLowerCase().trim();
    }

}

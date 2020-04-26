package recipebook.domain.ingredient;

import java.util.List;

import recipebook.dao.ingredientdao.IngredientDao;

/**
 * Contains the business logic for handling Ingredient objects. The dao
 * dependency is injected in the constructor.
 */
public class IngredientService {

    private IngredientDao ingDao;

    public IngredientService(IngredientDao ingDao) {
        this.ingDao = ingDao;
    }

    /**
     * Method to fetch all ingredients stored in the current data store.
     * 
     * @return List of ingredients.
     */
    public List<Ingredient> listAll() {
        return ingDao.getAll();
    }

    /**
     * Method to create a new ingredient and store it by the dao dependency. Checks
     * if the ingredient with same name and unit exists to avoid duplicate items.
     * 
     * @param name
     * @param unit
     * @return Returns the newly created Ingredient object or, if existent, an
     *         ingredient with the same name and unit.
     */
    public Ingredient createIngredient(String name, String unit) {
        Ingredient foundIngredient = ingDao.getByNameAndUnit(formatName(name), unit);
        if (foundIngredient == null) {
            return ingDao.create(new Ingredient(formatName(name), unit));
        }
        return foundIngredient;
    }

    /**
     * If the new ingredient's unit is not provided, it defaults to grams.
     * 
     * @see recipebook.domain.ingredient.IngredientService#createIngredient(String,
     *      String)
     * 
     * @param name
     * @return Returns the newly created Ingredient object or, if existent, an
     *         ingredient with the same name and unit.
     */
    public Ingredient createIngredient(String name) {
        return createIngredient(name, "g");
    }

    /**
     * Method to fetch all ingredients whose name matches the parameter name. The
     * ingredient names are case-insensitive and the leading or trailing spaces are
     * trimmed.
     * 
     * @param name
     * @return List of matching ingredients or an empty list if no matches are
     *         found.
     */
    public List<Ingredient> findByName(String name) {
        return ingDao.getByName(formatName(name));
    }

    /**
     * Method to fetch an ingredient with certain name and unit. Only one match is
     * expected as duplicates are not stored.
     * 
     * @param name
     * @param unit
     * @return A matching Ingredient object or null if no matches are found.
     */
    public Ingredient findByNameAndUnit(String name, String unit) {
        return ingDao.getByNameAndUnit(formatName(name), unit);
    }

    private String formatName(String name) {
        return name.toLowerCase().trim();
    }

}

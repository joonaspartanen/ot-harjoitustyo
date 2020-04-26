package recipebook.dao;

import java.io.File;

import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserDao;

/**
 * Abstract class that offers protected variables and protected and public
 * methods for connecting to a database or a file where the application data is
 * stored.
 */

public abstract class DataStoreConnector {

    protected IngredientDao ingredientDao;
    protected RecipeDao recipeDao;
    protected UserDao userDao;
    protected String dataStoreLocation;

    /**
     * Abstract method to initialize the data store. Different implementations for
     * database and file store.
     * 
     * @throws DatabaseException
     */
    public abstract void initializeDataStore() throws DatabaseException;

    /**
     * Abstract method to close the data store. Different implementations for
     * database and file store.
     * 
     * @throws DatabaseException
     */
    public abstract void closeDataStore() throws DatabaseException;

    public IngredientDao getIngredientDao() {
        return ingredientDao;
    }

    public RecipeDao getRecipeDao() {
        return recipeDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    protected void createDirectoryIfNotExists() {
        File directory = new File(dataStoreLocation);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
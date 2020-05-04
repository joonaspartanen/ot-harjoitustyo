package recipebook.dao;

import java.io.File;

import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;

/**
 * Abstract class that provides protected variables and protected and public
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
     * @throws DataStoreException, UserNotFoundException
     */
    public abstract void initializeDataStore() throws DataStoreException, UserNotFoundException;

    /**
     * Abstract method to close the data store. Different implementations for
     * database and file store.
     *
     * @throws DataStoreException
     */
    public abstract void closeDataStore() throws DataStoreException;

    public IngredientDao getIngredientDao() {
        return ingredientDao;
    }

    public RecipeDao getRecipeDao() {
        return recipeDao;
    }

    public UserDao getUserDao() {
        return userDao;
    }

    /**
     * Creates the directory structure to the data store location if missing.
     */
    protected void createDirectoryIfNotExists() {
        File directory = new File(dataStoreLocation);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}

package recipebook.dao;

import recipebook.dao.ingredientdao.FileIngredientDao;
import recipebook.dao.recipedao.FileRecipeDao;
import recipebook.dao.userdao.FileUserDao;
import recipebook.dao.userdao.UserNotFoundException;

/**
 * Handles the connection to the text files used as a data store and initializes
 * the DAO implementations used in the application.
 *
 * @see recipebook.dao.DataStoreConnector
 */
public class FileConnector extends DataStoreConnector {

    private String ingredientsFile;
    private String recipesFile;
    private String recipesIngredientsFile;
    private String userFile;
    private String favoriteRecipesFile;

    /**
     * Constructor
     *
     * @param dataStoreLocation Path to the folder where the data store file is
     *                          located.
     */
    public FileConnector(String dataStoreLocation) {
        this.dataStoreLocation = dataStoreLocation;
    }

    /**
     * Creates the data store directory if necessary, concatenates the path to data
     * store file names and creates the DAO implementations.
     * 
     * @throws recipebook.dao.userdao.UserNotFoundException
     */
    @Override
    public void initializeDataStore() throws DataStoreException, UserNotFoundException {
        createDirectoryIfNotExists();
        initializeFileNames();
        ingredientDao = new FileIngredientDao(ingredientsFile);
        userDao = new FileUserDao(userFile);
        recipeDao = new FileRecipeDao(ingredientDao, userDao, recipesFile, recipesIngredientsFile, favoriteRecipesFile);
    }

    private void initializeFileNames() {
        ingredientsFile = dataStoreLocation + "ingredients.txt";
        recipesFile = dataStoreLocation + "recipes.txt";
        recipesIngredientsFile = dataStoreLocation + "recipesIngredients.txt";
        userFile = dataStoreLocation + "users.txt";
        favoriteRecipesFile = dataStoreLocation + "favoriteRecipes.txt";
    }

    /**
     * Abstract method that needs no implementation when the data store is a file.
     */
    @Override
    public void closeDataStore() {
        // No implementation needed.
    }

}

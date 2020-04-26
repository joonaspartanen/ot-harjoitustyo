package recipebook.dao;

import recipebook.dao.ingredientdao.FileIngredientDao;
import recipebook.dao.recipedao.FileRecipeDao;

public class FileConnector extends DataStoreConnector {

    private String ingredientsFile;
    private String recipesFile;
    private String recipesIngredientsFile;

    /**
     * Constructor
     * 
     * @param dataStoreLocation Path to the folder where the datastore file is
     *                          located.
     */
    public FileConnector(String dataStoreLocation) {
        this.dataStoreLocation = dataStoreLocation;
    }

    /**
     * Creates the datastore directory if necessary, concatenates the path to
     * datastore file names and creates the dao implementations.
     */
    @Override
    public void initializeDataStore() {
        createDirectoryIfNotExists();
        initializeFileNames();
        ingredientDao = new FileIngredientDao(ingredientsFile);
        recipeDao = new FileRecipeDao(ingredientDao, recipesFile, recipesIngredientsFile);
    }

    private void initializeFileNames() {
        ingredientsFile = dataStoreLocation + "ingredients.txt";
        recipesFile = dataStoreLocation + "recipes.txt";
        recipesIngredientsFile = dataStoreLocation + "recipesIngredients.txt";
    }

    @Override
    public void closeDataStore() {
        // No implementation needed.
    }

}
package recipebook.dao;

import recipebook.dao.ingredientdao.FileIngredientDao;
import recipebook.dao.recipedao.FileRecipeDao;

public class FileConnector extends DataStoreConnector {

    String ingredientsFile;
    String recipesFile;
    String recipesIngredientsFile;

    public FileConnector(String dataStoreLocation) {
        this.dataStoreLocation = dataStoreLocation;
    }

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
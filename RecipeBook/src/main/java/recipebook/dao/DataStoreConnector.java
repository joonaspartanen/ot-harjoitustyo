package recipebook.dao;

import java.io.File;

import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.recipedao.RecipeDao;

public abstract class DataStoreConnector {

    protected IngredientDao ingredientDao;
    protected RecipeDao recipeDao;
    protected String dataStoreLocation;

    public abstract void initializeDataStore();

    public abstract void closeDataStore();

    public IngredientDao getIngredientDao() {
        return ingredientDao;
    }

    public RecipeDao getRecipeDao() {
        return recipeDao;
    }

    protected void createDirectoryIfNotExists() {
        File directory = new File(dataStoreLocation);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

}
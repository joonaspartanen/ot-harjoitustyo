package recipebook.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import recipebook.dao.ingredientdao.DatabaseIngredientDao;
import recipebook.dao.recipedao.DatabaseRecipeDao;

public class DatabaseConnector extends DataStoreConnector {

    private Connection connection = null;

    public DatabaseConnector(String dataStoreLocation) {
        this.dataStoreLocation = dataStoreLocation;
    }

    @Override
    public void initializeDataStore() {
        createDirectoryIfNotExists();
        connectToDatabase();
        createTables();
        createDaoImplementations();
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataStoreLocation + "recipes.db");
        } catch (SQLException e) {
            System.out.println("Connection to database failed.");
            e.printStackTrace();
        }
    }

    private void createTables() {
        String createRecipesTable = QueryBuilder.generateCreateRecipesTableQuery();
        String createIngredientsTable = QueryBuilder.generateCreateIngredientsTableQuery();
        String createRecipesIngredientsTable = QueryBuilder.generateCreateRecipesIngredientsTableQuery();

        PreparedStatement pstmt;

        try {
            pstmt = connection.prepareStatement(createRecipesTable);
            pstmt.executeUpdate();
            pstmt = connection.prepareStatement(createIngredientsTable);
            pstmt.executeUpdate();
            pstmt = connection.prepareStatement(createRecipesIngredientsTable);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Creating database tables failed.");
            e.printStackTrace();
        }
    }

    private void createDaoImplementations() {
        ingredientDao = new DatabaseIngredientDao(connection);
        recipeDao = new DatabaseRecipeDao(connection, ingredientDao);
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public void closeDataStore() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Closing database connection failed.");
            e.printStackTrace();
        }
    }
}
package recipebook.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import recipebook.dao.ingredientdao.DatabaseIngredientDao;
import recipebook.dao.recipedao.DatabaseRecipeDao;
import recipebook.dao.userdao.DatabaseUserDao;

/**
 * Handles the opening and closing the database connection and initializes the
 * DAO implementations used in the application. Extends DataStoreConnector.
 *
 * @see recipebook.dao.DataStoreConnector
 */
public class DatabaseConnector extends DataStoreConnector {

    private Connection connection = null;

    /**
     * Constructor.
     * 
     * @param dataStoreLocation Path to the folder where the database is located.
     *
     */
    public DatabaseConnector(String dataStoreLocation) {
        this.dataStoreLocation = dataStoreLocation;
    }

    /**
     * Creates the database directory if necessary, connects to the database file,
     * creates the database tables (if necessary), as well as the DAO
     * implementations.
     */
    @Override
    public void initializeDataStore() throws DataStoreException {
        createDirectoryIfNotExists();
        connectToDatabase();
        createTables();
        createDaoImplementations();
    }

    private void connectToDatabase() throws DataStoreException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataStoreLocation + "recipes.db");
        } catch (SQLException e) {
            throw new DataStoreException("Connection to database failed.", e);
        }
    }

    private void createTables() throws DataStoreException {
        createUsersTable();
        createIngredientsTable();
        createRecipesTable();
        createRecipesIngredientsTable();
        createFavoriteRecipesTable();
    }

    private void createRecipesTable() throws DataStoreException {
        String createRecipesTable = QueryBuilder.generateCreateRecipesTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createRecipesTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating Recipes table failed.", e);
        }
    }

    private void createIngredientsTable() throws DataStoreException {
        String createIngredientsTable = QueryBuilder.generateCreateIngredientsTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createIngredientsTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating Ingredients table failed.", e);
        }
    }

    private void createRecipesIngredientsTable() throws DataStoreException {
        String createRecipesIngredientsTable = QueryBuilder.generateCreateRecipesIngredientsTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createRecipesIngredientsTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating RecipesIngredients table failed.", e);
        }
    }

    private void createUsersTable() throws DataStoreException {
        String createUsersTable = QueryBuilder.generateCreateUsersTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createUsersTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating Users table failed.", e);
        }
    }

    private void createFavoriteRecipesTable() throws DataStoreException {
        String createFavoriteRecipesTable = QueryBuilder.generateCreateFavoriteRecipesTablesQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createFavoriteRecipesTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating FavoriteRecipes table failed.", e);
        }
    }

    private void createDaoImplementations() {
        userDao = new DatabaseUserDao(connection);
        ingredientDao = new DatabaseIngredientDao(connection);
        recipeDao = new DatabaseRecipeDao(connection, ingredientDao, userDao);
    }

    /**
     * Closes the database connection.
     *
     * @throws DataStoreException if closing the connection fails.
     */
    @Override
    public void closeDataStore() throws DataStoreException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DataStoreException("Closing database connection failed.", e);
        }
    }
}

package recipebook.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import recipebook.dao.ingredientdao.DatabaseIngredientDao;
import recipebook.dao.recipedao.DatabaseRecipeDao;
import recipebook.dao.userdao.DatabaseUserDao;

public class DatabaseConnector extends DataStoreConnector {

    private Connection connection = null;

    /**
     * Constructor
     * 
     * @param dataStoreLocation Path to the folder where the datastore file is
     *                          located.
     */
    public DatabaseConnector(String dataStoreLocation) {
        this.dataStoreLocation = dataStoreLocation;
    }

    /**
     * Creates the database directory if necessary, connects to the database file,
     * creates the database tables and dao implementations.
     */
    @Override
    public void initializeDataStore() throws DatabaseException {
        createDirectoryIfNotExists();
        connectToDatabase();
        createTables();
        createDaoImplementations();
    }

    private void connectToDatabase() throws DatabaseException {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataStoreLocation + "recipes.db");
        } catch (SQLException e) {
            throw new DatabaseException("Connection to database failed.", e);
        }
    }

    private void createTables() throws DatabaseException {
        createUsersTable();
        createIngredientsTable();
        createRecipesTable();
        createRecipesIngredientsTable();
        createFavoriteRecipesTable();
    }

    private void createRecipesTable() throws DatabaseException {
        String createRecipesTable = QueryBuilder.generateCreateRecipesTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createRecipesTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Creating Recipes table failed.", e);
        }
    }

    private void createIngredientsTable() throws DatabaseException {
        String createIngredientsTable = QueryBuilder.generateCreateIngredientsTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createIngredientsTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Creating Ingredients table failed.", e);
        }
    }

    private void createRecipesIngredientsTable() throws DatabaseException {
        String createRecipesIngredientsTable = QueryBuilder.generateCreateRecipesIngredientsTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createRecipesIngredientsTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Creating RecipesIngredients table failed.", e);
        }
    }

    private void createUsersTable() throws DatabaseException {
        String createUsersTable = QueryBuilder.generateCreateUsersTableQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createUsersTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Creating Users table failed.", e);
        }
    }

    private void createFavoriteRecipesTable() throws DatabaseException {
        String createFavoriteRecipesTable = QueryBuilder.generateCreateFavoriteRecipesTablesQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createFavoriteRecipesTable)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Creating FavoriteRecipes table failed.", e);
        }
    }

    private void createDaoImplementations() {
        ingredientDao = new DatabaseIngredientDao(connection);
        recipeDao = new DatabaseRecipeDao(connection, ingredientDao);
        userDao = new DatabaseUserDao(connection);
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Closes the database connection.
     * 
     * @throws DatabaseException if closing the connection fails.
     */
    @Override
    public void closeDataStore() throws DatabaseException {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Closing database connection failed.", e);
        }
    }
}
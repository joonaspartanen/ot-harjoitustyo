package recipebook.dao;

import java.sql.*;

public class DatabaseConnector {

    private Connection connection = null;
    private String databasePath;

    public DatabaseConnector(String databasePath) {
        this.databasePath = databasePath;
    }

    public Connection initializeDatabase() {
        connectToDatabase(databasePath);
        createTables();
        return connection;
    }

    private void connectToDatabase(String databasePath) {
        try {
            connection = DriverManager.getConnection(databasePath);
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

    public void closeConnection() {
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
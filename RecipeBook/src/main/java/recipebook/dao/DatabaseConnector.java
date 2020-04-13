package recipebook.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
        String createRecipesTable = "CREATE TABLE IF NOT EXISTS Recipes (id integer PRIMARY KEY NOT NULL UNIQUE, name varchar(30), time integer, instructions varchar(300));";
        String createIngredientsTable = "CREATE TABLE IF NOT EXISTS Ingredients (id integer PRIMARY KEY NOT NULL UNIQUE, name varchar(30), unit varchar(5));";
        String createRecipesIngredientsTable = "CREATE TABLE IF NOT EXISTS RecipesIngredients (id integer PRIMARY KEY NOT NULL UNIQUE, recipe_id integer, ingredient_id integer, "
                + "amount integer, FOREIGN KEY(recipe_id) REFERENCES Recipes(id), FOREIGN KEY(ingredient_id) REFERENCES Ingredients(id));";

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
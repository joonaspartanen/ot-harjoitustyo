package recipebook.dao;

/**
 * Provides SQL queries by static methods with self-explanatory names.
 */
public class QueryBuilder {

    private static final String SELECT_RECIPE_WITH_INGREDIENTS_QUERY = "SELECT Recipes.id AS recipe_id, Recipes.name AS recipe_name, Recipes.time, "
            + "Recipes.instructions, Recipes.author_id, Ingredients.id AS ingredient_id, Ingredients.name AS ingredient_name, Ingredients.unit AS ingredient_unit, "
            + "RecipesIngredients.amount FROM Recipes JOIN RecipesIngredients ON Recipes.id = RecipesIngredients.recipe_id "
            + "JOIN Ingredients ON RecipesIngredients.ingredient_id = Ingredients.id";

    private static final String SELECT_INGREDIENTS_QUERY = "SELECT id AS ingredient_id, name AS ingredient_name, unit AS ingredient_unit FROM Ingredients";

    public static String generateSelectAllRecipesQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + ";";
    }

    public static String generateSelectAllRecipesWhereRecipeNameLikeQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + " WHERE recipe_name LIKE ?;";
    }

    public static String generateSelectAllRecipesByRecipeIdQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + " WHERE recipe_id = ?;";
    }

    public static String generateSelectAllRecipesByIngredientIdQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + " WHERE ingredient_id = ?;";
    }

    public static String generateInsertRecipeQuery() {
        return "INSERT INTO Recipes (name, time, instructions, author_id) VALUES (?, ?, ?, ?);";
    }

    public static String generateInsertRecipesIngredientsQuery() {
        return "INSERT INTO RecipesIngredients (recipe_id, ingredient_id, amount) VALUES (?, ?, ?);";
    }

    public static String generateSelectAllIngredientsQuery() {
        return SELECT_INGREDIENTS_QUERY + ";";
    }

    public static String generateSelectAllIngredientsByNameQuery() {
        return SELECT_INGREDIENTS_QUERY + " WHERE name LIKE ?;";
    }

    public static String generateSelectAllIngredientsByIdQuery() {
        return SELECT_INGREDIENTS_QUERY + " WHERE id = ?;";
    }

    public static String generateSelectAllIngredientsByNameAndUnitQuery() {
        return SELECT_INGREDIENTS_QUERY + " WHERE name = ? AND unit = ?;";
    }

    public static String generateInsertIngredientQuery() {
        return "INSERT INTO Ingredients (name, unit) VALUES (?, ?);";
    }

    public static String generateDeleteFromRecipesByIdQuery(String tableName) {
        return "DELETE FROM " + tableName + " WHERE id = ?;";
    }

    public static String generateInsertUserQuery() {
        return "INSERT INTO Users (username) VALUES (?);";
    }

    public static String generateSelectUserByUsernameQuery() {
        return "SELECT id AS user_id, username FROM Users WHERE username = ?;";
    }

    public static String generateSelectUserByIdQuery() {
        return "SELECT id AS user_id, username FROM Users WHERE user_id = ?;";
    }

    public static String generateInsertIntoFavoriteRecipesQuery() {
        return "INSERT INTO FavoriteRecipes (user_id, recipe_id) values (?, ?);";
    }

    public static String generateCreateUsersTableQuery() {
        return "CREATE TABLE IF NOT EXISTS Users (id integer PRIMARY KEY NOT NULL UNIQUE, username VARCHAR(20));";
    }

    public static String generateCreateRecipesTableQuery() {
        return "CREATE TABLE IF NOT EXISTS Recipes (id INTEGER PRIMARY KEY NOT NULL UNIQUE, name VARCHAR(30), time INTEGER, "
                + "instructions VARCHAR(1000), author_id INTEGER, FOREIGN KEY(author_id) REFERENCES Users(id));";
    }

    public static String generateCreateIngredientsTableQuery() {
        return "CREATE TABLE IF NOT EXISTS Ingredients (id INTEGER PRIMARY KEY NOT NULL UNIQUE, name VARCHAR(30), unit VARCHAR(5));";
    }

    public static String generateCreateRecipesIngredientsTableQuery() {
        return "CREATE TABLE IF NOT EXISTS RecipesIngredients (id INTEGER PRIMARY KEY NOT NULL UNIQUE, recipe_id INTEGER, ingredient_id INTEGER, "
                + "amount INTEGER, FOREIGN KEY(recipe_id) REFERENCES Recipes(id), FOREIGN KEY(ingredient_id) REFERENCES Ingredients(id));";
    }

    public static String generateCreateFavoriteRecipesTablesQuery() {
        return "CREATE TABLE IF NOT EXISTS FavoriteRecipes (user_id INTEGER, recipe_id INTEGER, FOREIGN KEY(user_id) REFERENCES User(id), FOREIGN KEY(recipe_id) REFERENCES Recipe(id));";
    }

    public static String generateSelectFavoriteRecipesQuery() {
        return "SELECT Recipes.id AS recipe_id, Recipes.name AS recipe_name, Recipes.time, "
                + "Recipes.instructions, Recipes.author_id, Ingredients.id AS ingredient_id, Ingredients.name AS ingredient_name, Ingredients.unit AS ingredient_unit, "
                + "RecipesIngredients.amount, FavoriteRecipes.user_id FROM Recipes JOIN RecipesIngredients ON Recipes.id = RecipesIngredients.recipe_id "
                + "JOIN Ingredients ON RecipesIngredients.ingredient_id = Ingredients.id JOIN FavoriteRecipes ON Recipes.id = FavoriteRecipes.recipe_id "
                + "WHERE user_id = ?;";
    }

    public static String generateDeleteRecipeQuery() {
        return "DELETE FROM Recipes WHERE id = ?;";
    }

    public static String generateDeleteRecipeIngredientsQuery() {
        return "DELETE FROM RecipesIngredients WHERE recipe_id = ?;";
    }

    public static String generateDeleteRecipeFromFavoritesQuery() {
        return "DELETE FROM FavoriteRecipes WHERE recipe_id = ?;";
    }

}

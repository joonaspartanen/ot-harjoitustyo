package recipebook.dao;

import java.util.List;
import java.util.stream.Collectors;

import recipebook.domain.ingredient.Ingredient;

public class QueryBuilder {

    private static final String SELECT_RECIPE_WITH_INGREDIENTS_QUERY = "SELECT Recipes.id AS recipe_id, Recipes.name AS recipe_name, Recipes.time, "
            + "Recipes.instructions, Ingredients.id AS ingredient_id, Ingredients.name AS ingredient_name, Ingredients.unit AS ingredient_unit, "
            + "RecipesIngredients.amount FROM Recipes JOIN RecipesIngredients ON Recipes.id = RecipesIngredients.recipe_id "
            + "JOIN Ingredients ON RecipesIngredients.ingredient_id = Ingredients.id";

    private static final String SELECT_INGREDIENTS_QUERY = "SELECT id AS ingredient_id, name AS ingredient_name, unit AS ingredient_unit FROM Ingredients";

    public static String generateSelectAllRecipesQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + ";";
    }

    public static String generateSelectAllRecipesByRecipeNameQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + " WHERE recipe_name LIKE ?;";
    }

    public static String generateSelectAllRecipesByRecipeIdQuery() {
        return SELECT_RECIPE_WITH_INGREDIENTS_QUERY + " WHERE recipe_id = ?;";
    }

    public static String generateSelectAllRecipesByIngredientIdsQuery(List<Ingredient> ingredients) {
        List<String> ingredientIds = ingredients.stream().map(i -> "ingredient_id = " + Integer.toString(i.getId()))
                .collect(Collectors.toList());
        String ingredientIdsCondition = ingredientIds.stream().collect(Collectors.joining(" OR "));
        String selectByIngredientQuery = SELECT_RECIPE_WITH_INGREDIENTS_QUERY + " WHERE " + ingredientIdsCondition;
        return selectByIngredientQuery;
    }

    public static String generateInsertRecipeQuery() {
        return "INSERT INTO Recipes (name, time, instructions) VALUES (?, ?, ?);";
    }

    public static String generateInsertRecipesIngredientsQuery() {
        return "INSERT INTO RecipesIngredients (recipe_id, ingredient_id, amount) VALUES (?, ?, ?);";
    }

    public static String generateSelectAllIngredientsQuery() {
        return SELECT_INGREDIENTS_QUERY + ";";
    }

    public static String generateSelectAllIngredientsByNameQuery() {
        return SELECT_INGREDIENTS_QUERY + " WHERE name = ?;";
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
        return "DELETE FROM " + tableName + " WHERE id = ?";
    }

    public static String generateCreateRecipesTableQuery() {
        return "CREATE TABLE IF NOT EXISTS Recipes (id integer PRIMARY KEY NOT NULL UNIQUE, name varchar(30), time integer, instructions varchar(300));";
    }

    public static String generateCreateIngredientsTableQuery() {
        return "CREATE TABLE IF NOT EXISTS Ingredients (id integer PRIMARY KEY NOT NULL UNIQUE, name varchar(30), unit varchar(5));";
    }

    public static String generateCreateRecipesIngredientsTableQuery() {
        return "CREATE TABLE IF NOT EXISTS RecipesIngredients (id integer PRIMARY KEY NOT NULL UNIQUE, recipe_id integer, ingredient_id integer, "
                + "amount integer, FOREIGN KEY(recipe_id) REFERENCES Recipes(id), FOREIGN KEY(ingredient_id) REFERENCES Ingredients(id));";
    }

}

package recipebook.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;

public class ResultSetMapper {

    public List<Ingredient> extractIngredientList(PreparedStatement pstmt) throws SQLException {
        List<Ingredient> ingredients = new ArrayList<>();

        try (ResultSet resultSet = pstmt.executeQuery()) {
            while (resultSet.next()) {
                Ingredient ingredient = mapResultSetRowToIngredient(resultSet);
                ingredients.add(ingredient);
            }
        }

        return ingredients;
    }

    public Ingredient extractSingleIngredient(PreparedStatement pstmt) throws SQLException {
        Ingredient ingredient = null;

        try (ResultSet resultSet = pstmt.executeQuery()) {

            if (resultSetIsEmpty(resultSet)) {
                System.out.println("No ingredients found.");
                return ingredient;
            }

            ingredient = mapResultSetRowToIngredient(resultSet);
        }

        return ingredient;
    }

    private boolean resultSetIsEmpty(ResultSet resultSet) throws SQLException {
        return !resultSet.isBeforeFirst();
    }

    private Ingredient mapResultSetRowToIngredient(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("ingredient_id");
        String name = resultSet.getString("ingredient_name");
        String unit = resultSet.getString("ingredient_unit");
        Ingredient ingredient = new Ingredient(id, name, unit);
        return ingredient;
    }

    public List<Recipe> extractRecipeList(PreparedStatement pstmt) throws SQLException {
        ResultSet resultSet = pstmt.executeQuery();
        Map<Integer, Recipe> recipeMap = new HashMap<>();

        while (resultSet.next()) {
            Recipe recipe = null;
            int recipeId = resultSet.getInt("recipe_id");
            Ingredient ingredient = mapResultSetRowToIngredient(resultSet);
            int amount = resultSet.getInt("amount");

            if (recipeNotYetMapped(recipeMap, recipeId)) {
                recipe = mapResultSetRowToRecipe(resultSet, ingredient, amount, recipeId);
                recipeMap.put(recipeId, recipe);
            } else {
                recipe = recipeMap.get(recipeId);
                recipe.getIngredients().put(ingredient, amount);
            }
        }

        return recipeMap.values().stream().collect(Collectors.toList());
    }

    private Recipe mapResultSetRowToRecipe(ResultSet resultSet, Ingredient ingredient, int amount, int recipeId)
            throws SQLException {
        String recipeName = resultSet.getString("recipe_name");
        int time = resultSet.getInt("time");
        String instructions = resultSet.getString("instructions");

        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(ingredient, amount);

        Recipe recipe = new Recipe(recipeId, recipeName, ingredients, time, instructions);
        return recipe;
    }

    private boolean recipeNotYetMapped(Map<Integer, Recipe> recipeMap, int recipeId) {
        return !recipeMap.containsKey(recipeId);
    }
}
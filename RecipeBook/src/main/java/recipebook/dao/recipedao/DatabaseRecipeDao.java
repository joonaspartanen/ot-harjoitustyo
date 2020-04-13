package recipebook.dao.recipedao;

import recipebook.dao.ingredientdao.IngredientDao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import recipebook.dao.DaoHelper;

import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;

public class DatabaseRecipeDao implements RecipeDao {

    private Connection connection;
    private IngredientDao ingDao;
    private DaoHelper daoHelper;
    private String selectRecipeWithIngredientsQuery = "SELECT Recipes.id AS recipe_id, Recipes.name AS recipe_name, Recipes.time, "
            + "Recipes.instructions, Ingredients.id AS ingredient_id, Ingredients.name AS ingredient_name, Ingredients.unit, "
            + "RecipesIngredients.amount FROM Recipes JOIN RecipesIngredients ON Recipes.id = RecipesIngredients.recipe_id "
            + "JOIN Ingredients ON RecipesIngredients.ingredient_id = Ingredients.id";

    public DatabaseRecipeDao(Connection connection, IngredientDao ingDao) {
        this.connection = connection;
        this.ingDao = ingDao;
        daoHelper = new DaoHelper();
    }

    @Override
    public Recipe create(Recipe recipe) {
        String createRecipeQuery = "INSERT INTO Recipes (name, time, instructions) VALUES (?, ?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(createRecipeQuery, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, recipe.getName());
            pstmt.setInt(2, recipe.getTime());
            pstmt.setString(3, recipe.getInstructions());
            pstmt.executeUpdate();

            recipe.setId(daoHelper.getCreatedItemId(pstmt));

            saveRecipeIngredients(recipe);

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Creating recipe " + recipe.getName() + " failed.");
        }

        return recipe;
    }

    private void saveRecipeIngredients(Recipe recipe) {
        Map<Ingredient, Integer> ingredientAmounts = recipe.getIngredients();
        ingredientAmounts.keySet().stream().forEach(i -> {
            int ingredientId = i.getId();
            int ingredientAmount = ingredientAmounts.get(i);
            createCrossReferenceTableRows(recipe.getId(), ingredientId, ingredientAmount);
        });
    }

    @Override
    public List<Recipe> getAll() {
        String selectAllQuery = selectRecipeWithIngredientsQuery + ";";
        List<Recipe> recipes = new ArrayList<>();

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectAllQuery);
            ResultSet resultSet = pstmt.executeQuery();
            recipes = extractAllRecipesFromResultSet(resultSet);
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching the recipes from database failed.");
        }

        return recipes;
    }

    private List<Recipe> extractAllRecipesFromResultSet(ResultSet resultSet) throws SQLException {
        Map<Integer, Recipe> recipeMap = new HashMap<>();

        while (resultSet.next()) {
            Recipe recipe = null;
            int recipeId = resultSet.getInt("recipe_id");
            Ingredient ingredient = extractIngredientFromResultSetRow(resultSet);
            int amount = resultSet.getInt("amount");

            if (recipeNotYetMapped(recipeMap, recipeId)) {
                recipe = extractRecipeFromResultSetRow(resultSet, ingredient, amount, recipeId);
                recipeMap.put(recipeId, recipe);
            } else {
                recipe = recipeMap.get(recipeId);
                recipe.getIngredients().put(ingredient, amount);
            }
        }
        return recipeMap.values().stream().collect(Collectors.toList());
    }

    private Ingredient extractIngredientFromResultSetRow(ResultSet resultSet) throws SQLException {
        int ingredientId = resultSet.getInt("ingredient_id");
        String ingredientName = resultSet.getString("ingredient_name");
        String unit = resultSet.getString("unit");
        Ingredient ingredient = new Ingredient(ingredientId, ingredientName, unit);
        return ingredient;
    }

    private Recipe extractRecipeFromResultSetRow(ResultSet resultSet, Ingredient ingredient, int amount, int recipeId)
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

    @Override
    public List<Recipe> getByName(String recipeName) {
        List<Recipe> recipes = new ArrayList<>();
        String selectByNameQuery = selectRecipeWithIngredientsQuery + " WHERE recipe_name LIKE ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery);
            pstmt.setString(1, "%" + recipeName + "%");
            ResultSet resultSet = pstmt.executeQuery();
            recipes = extractAllRecipesFromResultSet(resultSet);
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching recipe with name " + recipeName + " from database failed.");
            e.printStackTrace();
        }

        return recipes;
    }

    @Override
    public Recipe getById(int id) {
        List<Recipe> recipes = new ArrayList<>();
        String selectByIdQuery = selectRecipeWithIngredientsQuery + " WHERE recipe_id = ?";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectByIdQuery);
            pstmt.setInt(1, id);
            ResultSet resultSet = pstmt.executeQuery();
            recipes = extractAllRecipesFromResultSet(resultSet);
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching recipe with id " + id + " from database failed.");
            e.printStackTrace();
        }

        return recipes.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }

    @Override
    public List<Recipe> getByIngredient(String ingredientName) {
        List<Integer> recipeIds = new ArrayList<>();
        List<Ingredient> matchingIngredients = ingDao.getByName(ingredientName);

        if (matchingIngredients.isEmpty()) {
            return Collections.emptyList();
        }

        String selectByIngredientIdsQuery = generateSelectByIngredientIdsQuery(matchingIngredients);

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectByIngredientIdsQuery);
            ResultSet resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                recipeIds.add(resultSet.getInt("recipe_id"));
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching recipes with ingredient " + ingredientName + " from database failed.");
            e.printStackTrace();
        }

        return getRecipesByIdList(recipeIds);
    }

    private String generateSelectByIngredientIdsQuery(List<Ingredient> ingredients) {
        List<String> ingredientIds = ingredients.stream().map(i -> "ingredient_id = " + Integer.toString(i.getId()))
                .collect(Collectors.toList());
        String ingredientIdsCondition = ingredientIds.stream().collect(Collectors.joining(" OR "));
        String selectByIngredientQuery = selectRecipeWithIngredientsQuery + " WHERE " + ingredientIdsCondition;
        return selectByIngredientQuery;
    }

    public List<Recipe> getRecipesByIdList(List<Integer> recipeIds) {
        List<Recipe> recipes = new ArrayList<>();

        for (Integer recipeId : recipeIds) {
            Recipe recipe = getById(recipeId);
            recipes.add(recipe);
        }
        return recipes;
    }

    @Override
    public void delete(Recipe recipe) {
        int recipeId = recipe.getId();

        try {
            deleteRecipeRow(recipeId);
            deleteRecipesIngredients(recipeId);
        } catch (SQLException e) {
            System.out.println("Deleting recipe with id " + recipeId + " failed.");
            e.printStackTrace();
        }
    }

    private void deleteRecipeRow(int recipeId) throws SQLException {
        String deleteRecipeQuery = "DELETE FROM Recipes WHERE id = ?";
        PreparedStatement pstmtRecipe = connection.prepareStatement(deleteRecipeQuery);
        pstmtRecipe.setInt(1, recipeId);
        pstmtRecipe.executeUpdate();
    }

    private void deleteRecipesIngredients(int recipeId) throws SQLException {
        String deleteRecipesIngredientsQuery = "DELETE FROM RecipesIngredients WHERE id = ?";
        PreparedStatement pstmtRecipeIngs = connection.prepareStatement(deleteRecipesIngredientsQuery);
        pstmtRecipeIngs.setInt(1, recipeId);
        pstmtRecipeIngs.executeUpdate();
    }

    private void createCrossReferenceTableRows(int recipeId, int ingredientId, int ingredientAmount) {
        String createRecipesIngredientsRow = "INSERT INTO RecipesIngredients (recipe_id, ingredient_id, amount) VALUES (?, ?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(createRecipesIngredientsRow);
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.setInt(3, ingredientAmount);

            pstmt.executeUpdate();

            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
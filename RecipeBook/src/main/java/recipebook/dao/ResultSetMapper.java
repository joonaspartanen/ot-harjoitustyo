package recipebook.dao;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.user.User;

/**
 * Helper class that offers methods for mapping database query result sets to
 * Java objects.
 */
public class ResultSetMapper {

    private UserDao userDao = null;

    /**
     * Constructor
     *
     * @param userDao Needed to find the recipe author on the basis of the author
     *                id.
     */
    public ResultSetMapper(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Takes a prepared statement as parameter, executes it and maps the result set
     * to a list of ingredients.
     *
     * @param pstmt Prepared statement that returns a result set when executed.
     * @return List of ingredients or an empty list if no results found.
     * @throws SQLException
     */
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

    /**
     * Takes a prepared statement as parameter, executes it and maps the result set
     * to a single Ingredient object. Used when only a single result is expected.
     *
     * @param pstmt Prepared statement that returns a result set when executed.
     * @return A single Ingredient object that matches the query.
     * @throws SQLException
     */
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

    /**
     * Takes a prepared statement as parameter, executes it and maps the result set
     * to a list of recipes.
     *
     * @param pstmt Prepared statement that returns a result set when executed.
     * @return List of recipes or an empty list if no results found.
     * @throws SQLException
     */
    public List<Recipe> extractRecipeList(PreparedStatement pstmt) throws SQLException, UserNotFoundException {
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
            throws SQLException, UserNotFoundException {
        String recipeName = resultSet.getString("recipe_name");
        int time = resultSet.getInt("time");
        String instructions = resultSet.getString("instructions");
        int authorId = resultSet.getInt("author_id");
        User author = userDao.getById(authorId);

        Map<Ingredient, Integer> ingredients = new HashMap<>();
        ingredients.put(ingredient, amount);

        Recipe recipe = new Recipe(recipeId, recipeName, ingredients, time, instructions, author);
        return recipe;
    }

    private boolean recipeNotYetMapped(Map<Integer, Recipe> recipeMap, int recipeId) {
        return !recipeMap.containsKey(recipeId);
    }

    /**
     * Takes a prepared statement as parameter, executes it and maps the result set
     * to a single User object. Used when only a single result is expected.
     *
     * @param pstmt Prepared statement that returns a result set when executed.
     * @return A single User object that matches the query.
     * @throws SQLException
     */
    public User extractSingleUser(PreparedStatement pstmt) throws SQLException {
        User user = null;

        try (ResultSet resultSet = pstmt.executeQuery()) {

            if (resultSetIsEmpty(resultSet)) {
                System.out.println("No users found.");
                return user;
            }

            user = mapResultSetRowToUser(resultSet);
        }

        return user;
    }

    private User mapResultSetRowToUser(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        String username = resultSet.getString("username");
        User user = new User(id, username);
        return user;
    }
}

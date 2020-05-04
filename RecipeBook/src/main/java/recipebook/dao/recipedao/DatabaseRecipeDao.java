package recipebook.dao.recipedao;

import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.userdao.UserDao;

import java.sql.*;
import java.util.*;

import recipebook.dao.DataStoreException;
import recipebook.dao.IdExtractor;
import recipebook.dao.QueryBuilder;
import recipebook.dao.ResultSetMapper;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;

/**
 * RecipeDao implementation for storing recipe-related data into a database.
 *
 */
public class DatabaseRecipeDao implements RecipeDao {

    private Connection connection;
    private IngredientDao ingredientDao;
    private IdExtractor idExtractor;
    private ResultSetMapper mapper;

    /**
     * Constructor.
     *
     * @param connection    The database connection.
     * @param ingredientDao An IngredientDao implementation needed to handle the
     *                      ingredients related to recipes.
     * @param userDao       An UserDao implementation needed to handle the recipe
     *                      author property.
     */
    public DatabaseRecipeDao(Connection connection, IngredientDao ingredientDao, UserDao userDao) {
        this.connection = connection;
        this.ingredientDao = ingredientDao;
        idExtractor = new IdExtractor();
        mapper = new ResultSetMapper(userDao);
    }

    /**
     * Stores the recipe into the database and sets its id.
     *
     * @param recipe The Recipe object to be stored.
     * @return The stored Recipe object with corresponding id.
     * @throws DataStoreException
     */
    @Override
    public Recipe create(Recipe recipe) throws DataStoreException {
        String insertRecipeQuery = QueryBuilder.generateInsertRecipeQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(insertRecipeQuery,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setInt(2, recipe.getTime());
            pstmt.setString(3, recipe.getInstructions());
            pstmt.setInt(4, recipe.getAuthorId());
            pstmt.executeUpdate();

            recipe.setId(idExtractor.getCreatedItemIdFromDatabase(pstmt));

            saveRecipeIngredients(recipe);
            saveRecipeToFavorites(recipe.getAuthorId(), recipe);
            return recipe;
        } catch (SQLException e) {
            throw new DataStoreException("Creating recipe " + recipe.getName() + " failed.", e);
        }
    }

    private void saveRecipeIngredients(Recipe recipe) throws DataStoreException {
        Map<Ingredient, Integer> ingredientAmounts = recipe.getIngredients();
        for (Ingredient ingredient : ingredientAmounts.keySet()) {
            int ingredientId = ingredient.getId();
            int ingredientAmount = ingredientAmounts.get(ingredient);
            createCrossReferenceTableRows(recipe.getId(), ingredientId, ingredientAmount);
        }
    }

    private void createCrossReferenceTableRows(int recipeId, int ingredientId, int ingredientAmount)
            throws DataStoreException {
        String insertRecipeIngredientsQuery = QueryBuilder.generateInsertRecipesIngredientsQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(insertRecipeIngredientsQuery)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.setInt(3, ingredientAmount);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating cross reference row for recipe id " + recipeId
                    + " and ingredient id " + ingredientId + " failed.", e);
        }
    }

    /**
     * Saves a recipe to user favorites.
     *
     * @param userId Id of the user who is adding the recipe to favorites.
     * @param recipe Recipe to be saved as a favorite.
     * @throws DataStoreException
     */
    public void saveRecipeToFavorites(int userId, Recipe recipe) throws DataStoreException {
        String insertIntoFavoritesQuery = QueryBuilder.generateInsertIntoFavoriteRecipesQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(insertIntoFavoritesQuery)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipe.getId());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataStoreException("Creating favorite recipe row failed.", e);
        }
    }

    /**
     * Fetches all recipes from the database.
     *
     * @return List of recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    @Override
    public List<Recipe> getAll() throws DataStoreException, UserNotFoundException {
        String selectAllQuery = QueryBuilder.generateSelectAllRecipesQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectAllQuery)) {
            List<Recipe> recipes = mapper.extractRecipeList(pstmt);
            return recipes;
        } catch (SQLException e) {
            throw new DataStoreException("Fetching the recipes from database failed.", e);
        }
    }

    /**
     * Fetches all recipes whose name contains the search term passed in as a
     * parameter.
     *
     * @param recipeName The recipe name used as a search term.
     * @return List of matching recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    @Override
    public List<Recipe> getByName(String recipeName) throws DataStoreException, UserNotFoundException {
        String selectByNameQuery = QueryBuilder.generateSelectAllRecipesWhereRecipeNameLikeQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery)) {
            pstmt.setString(1, "%" + recipeName + "%");
            List<Recipe> recipes = mapper.extractRecipeList(pstmt);
            return recipes;
        } catch (SQLException e) {
            throw new DataStoreException("Fetching recipe with name " + recipeName + " from database failed.", e);
        }
    }

    /**
     * Fetches the recipe whose id matches the search term passed in as a
     * parameter.Single result expected.
     *
     * @param id The recipe id used as a search term.
     * @return The matching recipe or null if no results are found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    @Override
    public Recipe getById(int id) throws DataStoreException, UserNotFoundException {
        String selectByIdQuery = QueryBuilder.generateSelectAllRecipesByRecipeIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByIdQuery)) {
            pstmt.setInt(1, id);
            List<Recipe> recipes = mapper.extractRecipeList(pstmt);
            return recipes.stream().filter(r -> r.getId() == id).findFirst()
                    .orElseThrow(() -> new DataStoreException("Recipe with id " + id + " was not found."));
        } catch (SQLException e) {
            throw new DataStoreException("Fetching recipe with id " + id + " from database failed.", e);
        }
    }

    /**
     * Fetches the recipes that contain the ingredient whose name is passed in as a
     * parameter.
     *
     * @param ingredientName The name of the ingredient used as a search term.
     * @return List of matching recipes or an empty list if no results found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    @Override
    public List<Recipe> getByIngredient(String ingredientName) throws DataStoreException, UserNotFoundException {
        List<Integer> recipeIds = new ArrayList<>();
        List<Ingredient> matchingIngredients = ingredientDao.getByName(ingredientName);

        if (matchingIngredients.isEmpty()) {
            return Collections.emptyList();
        }

        String selectByIngredientIdsQuery = QueryBuilder
                .generateSelectAllRecipesByIngredientIdsQuery(matchingIngredients);

        try (PreparedStatement pstmt = connection.prepareStatement(selectByIngredientIdsQuery);
                ResultSet resultSet = pstmt.executeQuery()) {

            while (resultSet.next()) {
                recipeIds.add(resultSet.getInt("recipe_id"));
            }

        } catch (SQLException e) {
            throw new DataStoreException(
                    "Fetching recipes with ingredient " + ingredientName + " from database failed.", e);
        }

        return getRecipesByIdList(recipeIds);
    }

    private List<Recipe> getRecipesByIdList(List<Integer> recipeIds) throws DataStoreException, UserNotFoundException {
        List<Recipe> recipes = new ArrayList<>();

        for (Integer recipeId : recipeIds) {
            Recipe recipe = getById(recipeId);
            recipes.add(recipe);
        }
        return recipes;
    }

    /**
     * Deletes a recipe from the database.
     *
     * @param recipe The recipe to be deleted.
     * @throws DataStoreException
     */
    @Override
    public void delete(Recipe recipe) throws DataStoreException {
        int recipeId = recipe.getId();

        try {
            deleteRecipeRow(recipeId);
            deleteRecipesIngredients(recipeId);
        } catch (SQLException e) {
            throw new DataStoreException("Deleting recipe with id " + recipeId + " failed.", e);
        }
    }

    /**
     * Fetches user's favorite recipes.
     *
     * @param userId Id of the user whose favorite recipes are fetched.
     * @return List of recipes or and empty list is no favorite recipes are found.
     * @throws DataStoreException
     * @throws UserNotFoundException
     */
    @Override
    public List<Recipe> getFavoriteRecipes(int userId) throws DataStoreException, UserNotFoundException {
        List<Recipe> favoriteRecipes = new ArrayList<>();

        String selectFavoriteRecipesQuery = QueryBuilder.generateSelectFavoriteRecipesQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectFavoriteRecipesQuery)) {
            pstmt.setInt(1, userId);
            favoriteRecipes = mapper.extractRecipeList(pstmt);
        } catch (SQLException e) {
            throw new DataStoreException("Fetching favorite recipes from the database failed.", e);
        }

        return favoriteRecipes;
    }

    private void deleteRecipeRow(int recipeId) throws SQLException {
        String deleteRecipeQuery = QueryBuilder.generateDeleteRecipeQuery();
        PreparedStatement pstmtRecipe = connection.prepareStatement(deleteRecipeQuery);
        pstmtRecipe.setInt(1, recipeId);
        pstmtRecipe.executeUpdate();
    }

    private void deleteRecipesIngredients(int recipeId) throws SQLException {
        String deleteRecipesIngredientsQuery = QueryBuilder.generateDeleteRecipeIngredientsQuery();
        PreparedStatement pstmtRecipeIngs = connection.prepareStatement(deleteRecipesIngredientsQuery);
        pstmtRecipeIngs.setInt(1, recipeId);
        pstmtRecipeIngs.executeUpdate();
    }

}

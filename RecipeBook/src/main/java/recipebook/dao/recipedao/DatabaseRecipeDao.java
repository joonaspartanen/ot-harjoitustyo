package recipebook.dao.recipedao;

import recipebook.dao.ingredientdao.IngredientDao;
import recipebook.dao.userdao.DatabaseUserDao;
import recipebook.dao.userdao.UserDao;

import java.sql.*;
import java.util.*;
import recipebook.dao.DaoHelper;
import recipebook.dao.QueryBuilder;
import recipebook.dao.ResultSetMapper;
import recipebook.domain.ingredient.Ingredient;
import recipebook.domain.recipe.Recipe;
import recipebook.domain.user.User;

public class DatabaseRecipeDao implements RecipeDao {

    private Connection connection;
    private IngredientDao ingDao;
    private DaoHelper daoHelper;
    private ResultSetMapper mapper;
    private UserDao userDao;

    public DatabaseRecipeDao(Connection connection, IngredientDao ingDao) {
        this.connection = connection;
        this.ingDao = ingDao;
        daoHelper = new DaoHelper();
        userDao = new DatabaseUserDao(connection);
        mapper = new ResultSetMapper(userDao);
    }

    @Override
    public Recipe create(Recipe recipe) {
        String createRecipeQuery = QueryBuilder.generateInsertRecipeQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createRecipeQuery,
                Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, recipe.getName());
            pstmt.setInt(2, recipe.getTime());
            pstmt.setString(3, recipe.getInstructions());
            pstmt.setInt(4, recipe.getAuthorId());
            pstmt.executeUpdate();

            recipe.setId(daoHelper.getCreatedItemId(pstmt));

            saveRecipeIngredients(recipe);
            saveRecipeToFavorites(recipe.getAuthorId(), recipe.getId());
        } catch (SQLException e) {
            System.out.println("Creating recipe " + recipe.getName() + " failed.");
            e.printStackTrace();
        }

        return recipe;
    }

    public void saveRecipeIngredients(Recipe recipe) {
        Map<Ingredient, Integer> ingredientAmounts = recipe.getIngredients();
        ingredientAmounts.keySet().stream().forEach(i -> {
            int ingredientId = i.getId();
            int ingredientAmount = ingredientAmounts.get(i);
            createCrossReferenceTableRows(recipe.getId(), ingredientId, ingredientAmount);
        });
    }

    private void createCrossReferenceTableRows(int recipeId, int ingredientId, int ingredientAmount) {
        String createRecipesIngredientsRow = QueryBuilder.generateInsertRecipesIngredientsQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createRecipesIngredientsRow)) {
            pstmt.setInt(1, recipeId);
            pstmt.setInt(2, ingredientId);
            pstmt.setInt(3, ingredientAmount);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Creating cross reference row for recipe id " + recipeId + " and ingredient id "
                    + ingredientId + " failed");
            e.printStackTrace();
        }
    }

    private void saveRecipeToFavorites(int userId, int recipeId) {
        String createFavoriteRecipe = "INSERT INTO FavoriteRecipes (user_id, recipe_id) values (?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(createFavoriteRecipe)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, recipeId);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Creating favorite recipe row failed.");
            e.printStackTrace();
        }
    }

    @Override
    public List<Recipe> getAll() {
        List<Recipe> recipes = new ArrayList<>();
        String selectAllQuery = QueryBuilder.generateSelectAllRecipesQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectAllQuery)) {
            recipes = mapper.extractRecipeList(pstmt);
        } catch (SQLException e) {
            System.out.println("Fetching the recipes from database failed.");
            e.printStackTrace();
        }

        return recipes;
    }

    @Override
    public List<Recipe> getByName(String recipeName) {
        List<Recipe> recipes = new ArrayList<>();
        String selectByNameQuery = QueryBuilder.generateSelectAllRecipesByRecipeNameQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery)) {
            pstmt.setString(1, "%" + recipeName + "%");
            recipes = mapper.extractRecipeList(pstmt);
        } catch (SQLException e) {
            System.out.println("Fetching recipe with name " + recipeName + " from database failed.");
            e.printStackTrace();
        }

        return recipes;
    }

    @Override
    public Recipe getById(int id) {
        List<Recipe> recipes = new ArrayList<>();
        String selectByIdQuery = QueryBuilder.generateSelectAllRecipesByRecipeIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByIdQuery)) {
            pstmt.setInt(1, id);
            recipes = mapper.extractRecipeList(pstmt);
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

        String selectByIngredientIdsQuery = QueryBuilder
                .generateSelectAllRecipesByIngredientIdsQuery(matchingIngredients);

        try (PreparedStatement pstmt = connection.prepareStatement(selectByIngredientIdsQuery);
                ResultSet resultSet = pstmt.executeQuery()) {

            while (resultSet.next()) {
                recipeIds.add(resultSet.getInt("recipe_id"));
            }

        } catch (SQLException e) {
            System.out.println("Fetching recipes with ingredient " + ingredientName + " from database failed.");
            e.printStackTrace();
        }

        return getRecipesByIdList(recipeIds);
    }

    private List<Recipe> getRecipesByIdList(List<Integer> recipeIds) {
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
        String deleteRecipesIngredientsQuery = "DELETE FROM RecipesIngredients WHERE recipe_id = ?";
        PreparedStatement pstmtRecipeIngs = connection.prepareStatement(deleteRecipesIngredientsQuery);
        pstmtRecipeIngs.setInt(1, recipeId);
        pstmtRecipeIngs.executeUpdate();
    }

}
package recipebook.dao.ingredientdao;

import java.sql.*;
import java.util.Collections;
import java.util.List;

import recipebook.dao.DaoHelper;
import recipebook.dao.QueryBuilder;
import recipebook.dao.ResultSetMapper;
import recipebook.dao.userdao.UserDao;
import recipebook.domain.ingredient.Ingredient;

/**
 * IngredientDao implementation to store Ingredient-related data into the
 * database.
 */
public class DatabaseIngredientDao implements IngredientDao {

    private Connection connection;
    private DaoHelper daoHelper;
    private ResultSetMapper mapper;

    /**
     * Constructor. The Connection dependency is passed as a parameter, and the
     * other object variables are instantiated here.
     * 
     * @param connection
     */
    public DatabaseIngredientDao(Connection connection, UserDao userDao) {
        this.connection = connection;
        daoHelper = new DaoHelper();
        mapper = new ResultSetMapper(userDao);
    }

    /**
     * Inserts the ingredient into the database and sets its id.
     * 
     * @param ingredient The ingredient to be stored.
     * @return Returns the stored ingredient.
     */
    @Override
    public Ingredient create(Ingredient ingredient) {
        String createIngredientQuery = QueryBuilder.generateInsertIngredientQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createIngredientQuery,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setString(2, ingredient.getUnit());
            pstmt.executeUpdate();

            int id = daoHelper.getCreatedItemId(pstmt);
            ingredient.setId(id);

        } catch (SQLException e) {
            System.out.println("Creating ingredient " + ingredient.getName() + " failed.");
            e.printStackTrace();
        }

        return ingredient;
    }

    /**
     * Fetches all ingredients from the database.
     * 
     * @return List of ingredients or an empty list if no matches found.
     */
    @Override
    public List<Ingredient> getAll() {
        String selectAllQuery = QueryBuilder.generateSelectAllIngredientsQuery();
        try (PreparedStatement pstmt = connection.prepareStatement(selectAllQuery)) {
            List<Ingredient> foundIngredients = mapper.extractIngredientList(pstmt);
            return foundIngredients;

        } catch (SQLException e) {
            System.out.println("Fetching the ingredients from database failed.");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * Fetches from the database all ingredients whose name contains the search term
     * received as a parameter.
     * 
     * @param name Search term
     * @return List of matching ingredients or an empty list if no matches are
     *         found.
     */
    @Override
    public List<Ingredient> getByName(String name) {
        String selectByNameQuery = QueryBuilder.generateSelectAllIngredientsByNameQuery();
        System.out.println(selectByNameQuery);
        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery)) {
            pstmt.setString(1, "%" + name + "%");

            List<Ingredient> foundIngredients = mapper.extractIngredientList(pstmt);
            return foundIngredients;

        } catch (SQLException e) {
            System.out.println("Fetching ingredient " + name + " from database failed.");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    /**
     * Fetches an ingredient by id. Only one result is expected.
     * 
     * @param id Id of the ingredient to be fetched.
     * @return The matching ingredient or null if not found.
     */
    @Override
    public Ingredient getById(int id) {
        Ingredient ingredient = null;
        String selectByIdQuery = QueryBuilder.generateSelectAllIngredientsByIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByIdQuery)) {
            pstmt.setInt(1, id);

            ingredient = mapper.extractSingleIngredient(pstmt);

        } catch (SQLException e) {
            System.out.println("Fetching ingredient with id " + id + " from database failed.");
            e.printStackTrace();
        }

        return ingredient;
    }

    /**
     * Fetches an ingredient by name and unit. Only one result is expected.
     * 
     * @param name Name of the searched ingredient.
     * @param unit Unit of the searched ingredient.
     * @return The matching ingredient or null if not found.
     */
    public Ingredient getByNameAndUnit(String name, String unit) {
        Ingredient ingredient = null;
        String selectByNameAndUnitQuery = QueryBuilder.generateSelectAllIngredientsByNameAndUnitQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameAndUnitQuery)) {
            pstmt.setString(1, name);
            pstmt.setString(2, unit);

            ingredient = mapper.extractSingleIngredient(pstmt);

        } catch (SQLException e) {
            System.out
                    .println("Fetching ingredient with name " + name + " and unit " + unit + " from database failed.");
            e.printStackTrace();
        }

        return ingredient;
    }

}
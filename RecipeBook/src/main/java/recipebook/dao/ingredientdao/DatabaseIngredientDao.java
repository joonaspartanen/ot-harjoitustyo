package recipebook.dao.ingredientdao;

import java.sql.*;
import java.util.List;

import recipebook.dao.*;
import recipebook.dao.userdao.DatabaseUserDao;
import recipebook.domain.ingredient.Ingredient;

/**
 * IngredientDao implementation to store ingredient-related data into the
 * database.
 */
public class DatabaseIngredientDao implements IngredientDao {

    private Connection connection;
    private IdExtractor idExtractor;
    private ResultSetMapper mapper;

    /**
     * Constructor. The Connection dependency is passed as a parameter, and the
     * other object variables are instantiated here.
     *
     * @param connection Connection to the database.
     */
    public DatabaseIngredientDao(Connection connection) {
        this.connection = connection;
        idExtractor = new IdExtractor();
        mapper = new ResultSetMapper(new DatabaseUserDao(connection));
    }

    /**
     * Inserts the ingredient into the database and sets its id.
     *
     * @param ingredient The ingredient to be stored.
     * @return Returns the stored ingredient.
     * @throws DataStoreException
     */
    @Override
    public Ingredient create(Ingredient ingredient) throws DataStoreException {
        String insertIngredientQuery = QueryBuilder.generateInsertIngredientQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(insertIngredientQuery,
                Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, ingredient.getName());
            pstmt.setString(2, ingredient.getUnit());
            pstmt.executeUpdate();

            int id = idExtractor.getCreatedItemIdFromDatabase(pstmt);
            ingredient.setId(id);

            return ingredient;
        } catch (SQLException e) {
            throw new DataStoreException("Creating ingredient " + ingredient.getName() + " failed.", e);
        }
    }

    /**
     * Fetches all ingredients from the database.
     *
     * @return List of ingredients or an empty list if no matches found.
     * @throws DataStoreException
     */
    @Override
    public List<Ingredient> getAll() throws DataStoreException {
        String selectAllQuery = QueryBuilder.generateSelectAllIngredientsQuery();
        try (PreparedStatement pstmt = connection.prepareStatement(selectAllQuery)) {
            List<Ingredient> foundIngredients = mapper.extractIngredientList(pstmt);
            return foundIngredients;
        } catch (SQLException e) {
            throw new DataStoreException("Fetching the ingredients from database failed.", e);
        }
    }

    /**
     * Fetches from the database all ingredients whose name contains the search term
     * received as a parameter.
     *
     * @param name Search term
     * @return List of matching ingredients or an empty list if no matches are
     *         found.
     * @throws DataStoreException
     */
    @Override
    public List<Ingredient> getByName(String name) throws DataStoreException {
        String selectByNameQuery = QueryBuilder.generateSelectAllIngredientsByNameQuery();
        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery)) {
            pstmt.setString(1, "%" + name + "%");

            List<Ingredient> foundIngredients = mapper.extractIngredientList(pstmt);
            return foundIngredients;
        } catch (SQLException e) {
            throw new DataStoreException("Fetching ingredient " + name + " from database failed.", e);
        }
    }

    /**
     * Fetches an ingredient by id. Only one result is expected.
     *
     * @param id Id of the ingredient to be fetched.
     * @return The matching ingredient,
     * @throws DataStoreException if the ingredient is not found or in case of SQL
     *                            exception.
     */
    @Override
    public Ingredient getById(int id) throws DataStoreException {
        Ingredient ingredient = null;
        String selectByIdQuery = QueryBuilder.generateSelectAllIngredientsByIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByIdQuery)) {
            pstmt.setInt(1, id);

            ingredient = mapper.extractSingleIngredient(pstmt);

            if (ingredient == null) {
                throw new DataStoreException("Ingredient with id " + id + " was not found.");
            }

            return ingredient;
        } catch (SQLException e) {
            throw new DataStoreException("Fetching ingredient with id " + id + " from database failed.", e);
        }
    }

    /**
     * Fetches an ingredient by name and unit. Only one result is expected.
     *
     * @param name Name of the searched ingredient.
     * @param unit Unit of the searched ingredient.
     * @return The matching ingredient.
     * @throws DataStoreException
     */
    public Ingredient getByNameAndUnit(String name, String unit) throws DataStoreException {
        String selectByNameAndUnitQuery = QueryBuilder.generateSelectAllIngredientsByNameAndUnitQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameAndUnitQuery)) {
            pstmt.setString(1, name);
            pstmt.setString(2, unit);

            Ingredient ingredient = mapper.extractSingleIngredient(pstmt);
            return ingredient;
        } catch (SQLException e) {
            throw new DataStoreException(
                    "Fetching ingredient with name " + name + " and unit " + unit + " from database failed.", e);
        }
    }
}

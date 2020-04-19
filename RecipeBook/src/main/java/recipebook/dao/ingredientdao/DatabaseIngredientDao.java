package recipebook.dao.ingredientdao;

import java.sql.*;
import java.util.Collections;
import java.util.List;

import recipebook.dao.DaoHelper;
import recipebook.dao.QueryBuilder;
import recipebook.dao.ResultSetMapper;
import recipebook.domain.ingredient.Ingredient;

public class DatabaseIngredientDao implements IngredientDao {

    private Connection connection;
    private DaoHelper daoHelper;
    private ResultSetMapper mapper;

    public DatabaseIngredientDao(Connection connection) {
        this.connection = connection;
        daoHelper = new DaoHelper();
        mapper = new ResultSetMapper();
    }

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

    @Override
    public List<Ingredient> getByName(String name) {
        String selectWhereNameQuery = QueryBuilder.generateSelectAllIngredientsByNameQuery();
        try (PreparedStatement pstmt = connection.prepareStatement(selectWhereNameQuery)) {
            pstmt.setString(1, name);

            List<Ingredient> foundIngredients = mapper.extractIngredientList(pstmt);
            return foundIngredients;

        } catch (SQLException e) {
            System.out.println("Fetching ingredient " + name + " from database failed.");
            e.printStackTrace();
        }

        return Collections.emptyList();
    }

    @Override
    public Ingredient getById(int id) {
        Ingredient ingredient = null;
        String selectByNameQuery = QueryBuilder.generateSelectAllIngredientsByIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery)) {
            pstmt.setInt(1, id);

            ingredient = mapper.extractSingleIngredient(pstmt);

        } catch (SQLException e) {
            System.out.println("Fetching ingredient with id " + id + " from database failed.");
            e.printStackTrace();
        }

        return ingredient;
    }

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
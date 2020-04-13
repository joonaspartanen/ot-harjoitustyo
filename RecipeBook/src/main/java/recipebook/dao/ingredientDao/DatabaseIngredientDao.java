package recipebook.dao.ingredientDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import recipebook.dao.DaoHelper;

import recipebook.domain.ingredient.Ingredient;

public class DatabaseIngredientDao implements IngredientDao {

    private Connection connection;
    private DaoHelper daoHelper;

    public DatabaseIngredientDao(Connection connection) {
        this.connection = connection;
        daoHelper = new DaoHelper();
    }

    @Override
    public Ingredient create(Ingredient ingredient) {
        String createIngredientQuery = "INSERT INTO Ingredients (name, unit) VALUES (?, ?);";

        try {
            PreparedStatement pstmt = connection.prepareStatement(createIngredientQuery,
                    Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, ingredient.getName());
            pstmt.setString(2, ingredient.getUnit());
            pstmt.executeUpdate();

            int id = daoHelper.getCreatedItemId(pstmt);
            ingredient.setId(id);

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Creating ingredient " + ingredient.getName() + " failed.");
        }

        return ingredient;
    }

    @Override
    public List<Ingredient> getAll() {
        List<Ingredient> ingredients = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM Ingredients;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectAllQuery);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = createIngredientFromResultSet(resultSet);
                ingredients.add(ingredient);
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching the ingredients from database failed.");
        }
        return ingredients;
    }

    private Ingredient createIngredientFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        String unit = resultSet.getString("unit");
        Ingredient ingredient = new Ingredient(id, name, unit);
        return ingredient;
    }

    @Override
    public List<Ingredient> getByName(String name) {
        List<Ingredient> ingredients = new ArrayList<>();
        String selectWhereNameQuery = "SELECT * FROM Ingredients WHERE name = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectWhereNameQuery);
            pstmt.setString(1, name);
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                Ingredient ingredient = createIngredientFromResultSet(resultSet);
                ingredients.add(ingredient);
            }

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching ingredient " + name + " from database failed.");
        }

        return ingredients;
    }

    @Override
    public Ingredient getById(int id) {
        Ingredient ingredient = null;
        String selectByNameQuery = "SELECT * FROM Ingredients WHERE id = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectByNameQuery);
            pstmt.setInt(1, id);
            ResultSet resultSet = pstmt.executeQuery();

            ingredient = createIngredientFromResultSet(resultSet);

            pstmt.close();
        } catch (SQLException e) {
            System.out.println("Fetching ingredient with id " + id + " from database failed.");
        }

        return ingredient;
    }

    public Ingredient getByNameAndUnit(String name, String unit) {
        Ingredient ingredient = null;
        String selectByNameAndUnitQuery = "SELECT * FROM Ingredients WHERE name = ? AND unit = ?;";

        try {
            PreparedStatement pstmt = connection.prepareStatement(selectByNameAndUnitQuery);
            pstmt.setString(1, name);
            pstmt.setString(2, unit);
            ResultSet resultSet = pstmt.executeQuery();

            ingredient = createIngredientFromResultSet(resultSet);

            pstmt.close();
        } catch (SQLException e) {
            System.out
                    .println("Fetching ingredient with name " + name + " and unit " + unit + " from database failed.");
        }

        return ingredient;
    }

}
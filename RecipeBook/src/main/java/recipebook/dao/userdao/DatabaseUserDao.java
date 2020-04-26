package recipebook.dao.userdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import recipebook.dao.DaoHelper;
import recipebook.dao.QueryBuilder;
import recipebook.dao.ResultSetMapper;
import recipebook.domain.user.User;

public class DatabaseUserDao implements UserDao {

    private Connection connection;
    private DaoHelper daoHelper;
    private ResultSetMapper mapper;

    public DatabaseUserDao(Connection connection) {
        this.connection = connection;
        daoHelper = new DaoHelper();
        mapper = new ResultSetMapper(this);
    }

    @Override
    public User create(User user) {
        String createUserQuery = QueryBuilder.generateInsertUserQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createUserQuery, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();

            user.setId(daoHelper.getCreatedItemId(pstmt));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public User getByUsername(String username) throws UserNotFoundException {
        User user = null;
        String getUserByUsernameQuery = QueryBuilder.generateSelectUserByUsernameQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(getUserByUsernameQuery)) {
            pstmt.setString(1, username);
            pstmt.executeQuery();

            user = mapper.extractSingleUser(pstmt);

        } catch (SQLException e) {
            throw new UserNotFoundException("Fetching user " + username + " failed.", e);
        }

        if (user == null) {
            throw new UserNotFoundException("User " + username + " was not found.");
        }

        return user;
    }

    @Override
    public User getById(int id) {
        User user = null;

        String getUserByIdQuery = QueryBuilder.generateSelectUserByIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(getUserByIdQuery)) {
            pstmt.setInt(1, id);
            pstmt.executeQuery();

            user = mapper.extractSingleUser(pstmt);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

}
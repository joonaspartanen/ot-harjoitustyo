package recipebook.dao.userdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import recipebook.dao.DataStoreException;
import recipebook.dao.IdExtractor;
import recipebook.dao.QueryBuilder;
import recipebook.dao.ResultSetMapper;
import recipebook.domain.user.User;

/**
 * UserDao implementation for storing data into a database.
 *
 */
public class DatabaseUserDao implements UserDao {

    private Connection connection;
    private IdExtractor idExtractor;
    private ResultSetMapper mapper;

    /**
     * Constructor.
     * 
     * @param connection Connection to the database.
     */
    public DatabaseUserDao(Connection connection) {
        this.connection = connection;
        idExtractor = new IdExtractor();
        mapper = new ResultSetMapper(this);
    }

    /**
     * Stores a new user.
     * 
     * @param user User to be stored.
     * @return The stored user with corresponding id.
     * @throws DataStoreException
     */
    @Override
    public User create(User user) throws DataStoreException {
        String createUserQuery = QueryBuilder.generateInsertUserQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(createUserQuery, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getUsername());
            pstmt.executeUpdate();

            user.setId(idExtractor.getCreatedItemIdFromDatabase(pstmt));

        } catch (SQLException ex) {
            throw new DataStoreException("Storing the user data into the database failed.", ex);
        }

        return user;
    }

    /**
     * Fetches the user whose username matches the search term passed in as a
     * parameter. Single result expected.
     *
     * @param username Username used as a search term.
     * @return The matching user.
     * @throws UserNotFoundException if no matches found.
     */
    @Override
    public User getByUsername(String username) throws UserNotFoundException {
        User user = null;
        String getUserByUsernameQuery = QueryBuilder.generateSelectUserByUsernameQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(getUserByUsernameQuery)) {
            pstmt.setString(1, username);

            user = mapper.extractSingleUser(pstmt);

        } catch (SQLException e) {
            throw new UserNotFoundException("Fetching user " + username + " failed.", e);
        }

        if (user == null) {
            throw new UserNotFoundException("User " + username + " was not found.");
        }

        return user;
    }

    /**
     * Fetches the user whose id matches the search term passed in as a parameter.
     *
     * @param id Id used as a search term.
     * @return The matching user.
     * @throws UserNotFoundException
     */
    @Override
    public User getById(int id) throws UserNotFoundException {
        User user = null;

        String getUserByIdQuery = QueryBuilder.generateSelectUserByIdQuery();

        try (PreparedStatement pstmt = connection.prepareStatement(getUserByIdQuery)) {
            pstmt.setInt(1, id);
            user = mapper.extractSingleUser(pstmt);

        } catch (SQLException ex) {
            throw new UserNotFoundException("Fetching the user from the database failed.", ex);
        }

        return user;
    }
}

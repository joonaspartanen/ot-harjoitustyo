package recipebook.dao.userdao;

import recipebook.dao.DataStoreException;
import recipebook.domain.user.User;

/**
 * DAO interface for handling user-related data.
 */
public interface UserDao {

    /**
     * Stores an user to the data store.
     *
     * @param user User to be stored.
     * @return The stored user with corresponding id.
     * @throws DataStoreException
     */
    User create(User user) throws DataStoreException;

    /**
     * Fetches the user whose username matches the search term passed in as a
     * parameter. Single result expected.
     *
     * @param username Username used as a search term.
     * @return The matching user.
     * @throws UserNotFoundException if no matches found.
     */
    User getByUsername(String username) throws UserNotFoundException;

    /**
     * Fetches the user whose id matches the search term passed in as a parameter.
     *
     * @param id Id used as a search term.
     * @return The matching user.
     * @throws UserNotFoundException
     *
     */
    User getById(int id) throws UserNotFoundException;

}

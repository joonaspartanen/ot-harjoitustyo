package recipebook.dao.userdao;

import recipebook.domain.user.User;

/**
 * Dao interface for handling User-related data. 
 */
public interface UserDao {

    User create(User user);

    User getByUsername(String username) throws UserNotFoundException;

    User getById(int id);

}
package recipebook.dao.userdao;

import recipebook.domain.user.User;

public interface UserDao {

    User create(User user);

    User getByUsername(String username) throws UserNotFoundException;

    User getById(int id);

}
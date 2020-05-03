package recipebook.domain.user;

import java.util.ArrayList;
import java.util.List;

import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;

public class UserDaoMock implements UserDao {

    List<User> users;

    public UserDaoMock() {
        users = new ArrayList<>();
    }

    @Override
    public User create(User user) {
        users.add(user);
        user.setId(users.size());
        return user;
    }

    @Override
    public User getByUsername(String username) throws UserNotFoundException {
        return users.stream().filter(u -> u.getUsername() == username).findFirst().orElse(null);
    }

    @Override
    public User getById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    
}
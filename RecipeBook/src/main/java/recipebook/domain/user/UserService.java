package recipebook.domain.user;

import recipebook.dao.DataStoreException;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;

/**
 * Contains the business logic for handling User objects. The UserDao dependency
 * is injected in the constructor.
 */
public class UserService {

    private User currentUser = null;
    private UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Finds the user by username and sets it as the current user.
     * 
     * @param username Username of the user who is logging in.
     * @return User object representing the current user.
     * @throws UserNotFoundException if username does not match any existing user.
     */
    public User login(String username) throws UserNotFoundException {
        currentUser = userDao.getByUsername(username);
        return currentUser;
    }

    /**
     * Sets currentUser to null, when user logs out.
     */
    public void logout() {
        currentUser = null;
    }

    /**
     * Used to create a new user. Checks if the username is valid or already taken.
     * 
     * @param username Desired username.
     * @return The newly created User object is returned if creation is successful.
     * @throws BadUsernameException if username is not valid or is already taken.
     * @throws DataStoreException
     */
    public User createUser(String username) throws BadUsernameException, DataStoreException {

        if (username.length() < 5 || username.length() > 20) {
            throw new BadUsernameException("The username must be 5-20 characters long.");
        }

        if (username.contains(";")) {
            throw new BadUsernameException("The username can't contain semicolons.");
        }

        if (usernameTaken(username)) {
            throw new BadUsernameException(
                    "Username " + username + " is already taken. Please choose another username.");
        }

        User user = new User(username);

        return userDao.create(user);
    }

    private boolean usernameTaken(String username) {
        try {
            return userDao.getByUsername(username) != null;
        } catch (UserNotFoundException e) {
            return false;
        }
    }

    /**
     * Returns the user that is currently using the application.
     * 
     * @return
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Method to check whether some user is logged in.
     * 
     * @return true is a user is logged in and false if no user is logged in.
     */
    public boolean userNotLoggedIn() {
        return currentUser == null;
    }

}
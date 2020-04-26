package recipebook.domain.user;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import recipebook.TestHelper;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;

public class UserServiceTest {

    UserService userService;
    UserDao userDao;
    TestHelper helper;

    public UserServiceTest() {
        userDao = new UserDaoMock();
        userService = new UserService(userDao);
        helper = new TestHelper();
    }

    @Test
    public void loginReturnsRightUser() throws UserNotFoundException, BadUsernameException {
        User user = helper.getTestUser();
        userDao.create(user);

        User currentUser = userService.login("Test user");

        assertThat(currentUser.getUsername(), is(equalTo("Test user")));
    }

    @Test
    public void logoutClearsCurrentUser() throws UserNotFoundException, BadUsernameException {
        User user = helper.getTestUser();
        userDao.create(user);
        userService.login("Test user");

        userService.logout();

        assertThat(userService.getCurrentUser(), is(nullValue()));
    }

    @Test
    public void createUserReturnsRightUser() throws BadUsernameException {
        User user = userService.createUser("Other tester");

        assertThat(user.getUsername(), is(equalTo("Other tester")));
    }

    @Test
    public void userNotLoggedInReturnsFalseIfUserLoggedIn() throws UserNotFoundException, BadUsernameException {
        User user = helper.getTestUser();
        userDao.create(user);

        userService.login("Test user");

        assertFalse(userService.userNotLoggedIn());
    }

    @Test
    public void userNotLoggedInReturnsTrueIfUserNotLoggedIn() {
        userService.logout();

        assertTrue(userService.userNotLoggedIn());
    }

}
package recipebook.domain.user;

import recipebook.dao.DataStoreException;
import recipebook.dao.userdao.UserDaoMock;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import recipebook.TestHelper;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;

public class UserServiceTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    UserService userService;
    UserDao userDao;
    TestHelper helper;

    public UserServiceTest() throws DataStoreException {
        userDao = new UserDaoMock();
        userService = new UserService(userDao);
        helper = new TestHelper();
    }

    @Test
    public void loginReturnsRightUser() throws UserNotFoundException, BadUsernameException, DataStoreException {
        User user = helper.getTestUser();
        userDao.create(user);

        User currentUser = userService.login("Test user");

        assertThat(currentUser.getUsername(), is(equalTo("Test user")));
    }

    @Test
    public void logoutClearsCurrentUser() throws UserNotFoundException, BadUsernameException, DataStoreException {
        User user = helper.getTestUser();
        userDao.create(user);
        userService.login("Test user");

        userService.logout();

        assertThat(userService.getCurrentUser(), is(nullValue()));
    }

    @Test
    public void createUserReturnsRightUser() throws BadUsernameException, DataStoreException {
        User user = userService.createUser("Other tester");

        assertThat(user.getUsername(), is(equalTo("Other tester")));
    }

    @Test
    public void createUserThrowsExceptionIfUsernameTooShort() throws BadUsernameException, DataStoreException {
        exceptionRule.expect(BadUsernameException.class);
        exceptionRule.expectMessage("The username must be 5-20 characters long.");

        userService.createUser("1234");
    }

    @Test
    public void createUserThrowsExceptionIfUsernameTooLong() throws BadUsernameException, DataStoreException {
        exceptionRule.expect(BadUsernameException.class);
        exceptionRule.expectMessage("The username must be 5-20 characters long.");

        userService.createUser("Very long username that should throw exception");
    }

    @Test
    public void createUserThrowsExceptionIfUsernameContainsSemicolons() throws BadUsernameException,
            DataStoreException {
        exceptionRule.expect(BadUsernameException.class);
        exceptionRule.expectMessage("The username can't contain semicolons.");

        userService.createUser("My; username");
    }

    @Test
    public void createUserThrowsExceptionIfUsernameAlreadyTaken() throws BadUsernameException, DataStoreException {
        exceptionRule.expect(BadUsernameException.class);
        exceptionRule.expectMessage("Username Tester is already taken. Please choose another username.");

        userService.createUser("Tester");
        userService.createUser("Tester");
    }


    @Test
    public void userNotLoggedInReturnsFalseIfUserLoggedIn() throws UserNotFoundException, BadUsernameException,
            DataStoreException {
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
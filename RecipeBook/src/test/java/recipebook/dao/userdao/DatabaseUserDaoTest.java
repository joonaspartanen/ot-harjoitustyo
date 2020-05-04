package recipebook.dao.userDao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.Connection;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import recipebook.dao.DataStoreConnector;
import recipebook.dao.DatabaseConnector;
import recipebook.dao.DatabaseException;
import recipebook.dao.userdao.UserDao;
import recipebook.dao.userdao.UserNotFoundException;
import recipebook.domain.user.User;

public class DatabaseUserDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    UserDao userDao;
    DataStoreConnector connector;
    Connection connection;

    @Before
    public void setUp() throws DatabaseException {
        connector = new DatabaseConnector(testFolder.getRoot().toString() + "/");
        connector.initializeDataStore();
        userDao = connector.getUserDao();
    }

    @After
    public void finalize() throws DatabaseException {
        connector.closeDataStore();
    }

    @Test
    public void createdUserHasRightUsername() {
        User user = userDao.create(new User("Tester"));
        assertThat(user.getUsername(), is(equalTo("Tester")));
    }

    @Test
    public void createdUserHasRightId() {
        User firstUser = userDao.create(new User("Tester 1"));
        User secondUser = userDao.create(new User("Tester 2"));

        assertThat(firstUser.getId(), is(1));
        assertThat(secondUser.getId(), is(2));
    }

    @Test
    public void getByUsernameReturnsRightUser() throws UserNotFoundException {
        createTestUsers(2);

        User user = userDao.getByUsername("Tester 1");

        assertThat(user.getUsername(), is(equalTo("Tester 1")));
    }

    @Test
    public void getByIdReturnsRightUser() {
        createTestUsers(2);

        User firstUser = userDao.getById(1);
        User secondUser = userDao.getById(2);

        assertThat(firstUser.getUsername(), is(equalTo("Tester 1")));
        assertThat(secondUser.getUsername(), is(equalTo("Tester 2")));
    }

    @Test
    public void getByIdReturnsNullIfUserNotFound() {
        createTestUsers(2);

        User user = userDao.getById(3);

        assertThat(user, is(nullValue()));
    }

    private void createTestUsers(int amount) {
        for (int i = 1; i <= amount; i++) {
            User user = new User("Tester " + i);
            userDao.create(user);
        }
    }
}

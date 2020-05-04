package recipebook.dao.userdao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.Connection;

import org.junit.*;
import org.junit.rules.TemporaryFolder;

import recipebook.dao.*;
import recipebook.domain.user.User;

public class DatabaseUserDaoTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    UserDao userDao;
    DataStoreConnector connector;
    Connection connection;

    @Before
    public void setUp() throws DataStoreException, UserNotFoundException {
        connector = new DatabaseConnector(testFolder.getRoot().toString() + "/");
        connector.initializeDataStore();
        userDao = connector.getUserDao();
    }

    @After
    public void finalize() throws DataStoreException {
        connector.closeDataStore();
    }

    @Test
    public void createdUserHasRightUsername() throws DataStoreException {
        User user = userDao.create(new User("Tester"));
        assertThat(user.getUsername(), is(equalTo("Tester")));
    }

    @Test
    public void createdUserHasRightId() throws DataStoreException {
        User firstUser = userDao.create(new User("Tester 1"));
        User secondUser = userDao.create(new User("Tester 2"));

        assertThat(firstUser.getId(), is(1));
        assertThat(secondUser.getId(), is(2));
    }

    @Test
    public void getByUsernameReturnsRightUser() throws UserNotFoundException, DataStoreException {
        createTestUsers(2);

        User user = userDao.getByUsername("Tester 1");

        assertThat(user.getUsername(), is(equalTo("Tester 1")));
    }

    @Test
    public void getByIdReturnsRightUser() throws UserNotFoundException, DataStoreException {
        createTestUsers(2);

        User firstUser = userDao.getById(1);
        User secondUser = userDao.getById(2);

        assertThat(firstUser.getUsername(), is(equalTo("Tester 1")));
        assertThat(secondUser.getUsername(), is(equalTo("Tester 2")));
    }

    @Test
    public void getByIdReturnsNullIfUserNotFound() throws UserNotFoundException, DataStoreException {
        createTestUsers(2);

        User user = userDao.getById(3);

        assertThat(user, is(nullValue()));
    }

    private void createTestUsers(int amount) throws DataStoreException {
        for (int i = 1; i <= amount; i++) {
            User user = new User("Tester " + i);
            userDao.create(user);
        }
    }
}

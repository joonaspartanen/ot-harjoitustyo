package recipebook.dao.userdao;

import java.io.File;
import java.io.IOException;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import recipebook.dao.DataStoreException;
import recipebook.domain.user.User;

public class FileUserDaoTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    private UserDao userDao;
    private File testUserFile;

    @Before
    public void setUp() {
        try {
            testUserFile = testFolder.newFile("testUsers.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
        userDao = new FileUserDao(testUserFile.getAbsolutePath());
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
    public void getByUsernameThrowsExceptionlIfNotFound() throws UserNotFoundException, DataStoreException {
        exceptionRule.expect(UserNotFoundException.class);
        exceptionRule.expectMessage("User Nonexisting was not found.");

        userDao.create(new User("Tester"));
        userDao.getByUsername("Nonexisting");
    }

    @Test
    public void getByIdReturnsRightUser() throws DataStoreException, UserNotFoundException {
        userDao.create(new User("Tester 1"));
        userDao.create(new User("Tester 2"));

        User user = userDao.getById(2);

        assertThat(user.getUsername(), is(equalTo("Tester 2")));
    }

    @Test
    public void getByIdReturnsNullIfNotFound() throws UserNotFoundException, DataStoreException {
        userDao.create(new User("Tester"));

        User user = userDao.getById(3);
        assertThat(user, is(nullValue()));
    }

    @Test
    public void rightUsersAreLoadedWhenObjectInstantiated() throws DataStoreException, UserNotFoundException {
        userDao.create(new User("Tester 1"));
        userDao.create(new User("Tester 2"));

        UserDao newUserDao = new FileUserDao(testUserFile.getAbsolutePath());

        User testerOne = newUserDao.getByUsername("Tester 1");
        User testerTwo = newUserDao.getByUsername("Tester 2");

        assertThat(testerOne.getUsername(), is(equalTo("Tester 1")));
        assertThat(testerOne.getId(), is(1));
        assertThat(testerTwo.getUsername(), is(equalTo("Tester 2")));
        assertThat(testerTwo.getId(), is(2));
    }

}

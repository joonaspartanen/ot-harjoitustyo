package recipebook.dao.userdao;

import java.io.*;
import java.util.*;

import recipebook.dao.DataStoreException;
import recipebook.dao.IdExtractor;
import recipebook.domain.user.User;

/**
 * UserDao implementation for storing data into a text file.
 *
 */
public class FileUserDao implements UserDao {

    private List<User> users;
    String userFile;
    IdExtractor idExtractor;

    /**
     * Constructor.
     * 
     * @param userFile Path to the file where the data is stored.
     */
    public FileUserDao(String userFile) {
        this.userFile = userFile;
        users = new ArrayList<>();
        idExtractor = new IdExtractor();
        readUsersFromFile();
    }

    /**
     * Stores an user to the data store.
     *
     * @param user User to be stored.
     * @return The stored user with corresponding id.
     * @throws DataStoreException
     */
    @Override
    public User create(User user) throws DataStoreException {
        user.setId(idExtractor.getCreatedItemIdFromList(users));
        users.add(user);
        writeUsersToFile();
        return user;
    }

    /**
     * Fetches the user whose username matches the search term passed in as a
     * parameter. Single result expected.
     *
     * @param username Username used as a search term.
     * @return The matching user.
     * @throws UserNotFoundException if no matches found.
     */
    @Override
    public User getByUsername(String username) throws UserNotFoundException {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst()
                .orElseThrow(() -> new UserNotFoundException("User " + username + " was not found."));
    }

    /**
     * Fetches the user whose id matches the search term passed in as a parameter.
     *
     * @param id Id used as a search term.
     * @return The matching user.
     * @throws UserNotFoundException
     *
     */
    @Override
    public User getById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    private void readUsersFromFile() {
        try (Scanner reader = new Scanner(new File(userFile))) {
            while (reader.hasNextLine()) {
                readSingleUser(reader);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + userFile + " will be created.");
        }
    }

    private void readSingleUser(Scanner reader) {
        String[] parts = reader.nextLine().split(";");
        int userId = Integer.parseInt(parts[0]);
        String username = parts[1];
        User user = new User(userId, username);
        users.add(user);
    }

    private void writeUsersToFile() throws DataStoreException {
        try (FileWriter writer = new FileWriter(new File(userFile))) {
            for (User user : users) {
                writer.write(user.getId() + ";" + user.getUsername() + "\n");
            }
        } catch (IOException e) {
            throw new DataStoreException("Saving the user data into a file failed.", e);
        }
    }
}

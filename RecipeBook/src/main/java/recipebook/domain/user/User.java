package recipebook.domain.user;

/**
 * Class that represents a user of the application.
 */
public class User {

    private int id;
    private String username;

    public User(String username) {
        this.username = username;
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return username;
    }

    public int getId() {
        return id;
    }

}
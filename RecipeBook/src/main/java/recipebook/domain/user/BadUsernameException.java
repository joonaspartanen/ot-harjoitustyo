package recipebook.domain.user;

/**
 * BadUserNameException is thrown if the username for a new user is not valid.
 * The exception is caught in the graphic UI and a descriptive error message is
 * displayed to the user.
 */
public class BadUsernameException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     * 
     * @param errorMessage Descriptive error message.
     */
    public BadUsernameException(String errorMessage) {
        super(errorMessage);
    }
}

package recipebook.dao.userdao;

/**
 * If user is not found a custom UserNotFoundException is thrown. It will be
 * caught in the UI and a descriptive error message is displayed to the user.
 */
public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = -1096606910660644477L;

    /**
     * If the failure is related to other exception (e.g. SQLException), the
     * original exception is wrapped into a UserNotFoundException.
     * 
     * @param errorMessage      Descriptive message
     * @param originalException The original exception is wrapped into
     *                          DatabaseException in order to conserve the original
     *                          stack trace for debugging purposes.
     */
    public UserNotFoundException(String errorMessage, Throwable originalException) {
        super(errorMessage, originalException);
    }

    /**
     * Constructor for cases where no other exception is caught.
     * 
     * @param errorMessage Descriptive error message.
     */
    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
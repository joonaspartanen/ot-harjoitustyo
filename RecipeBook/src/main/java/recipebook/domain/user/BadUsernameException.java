package recipebook.domain.user;

public class BadUsernameException extends Exception {

    private static final long serialVersionUID = 1L;

    public BadUsernameException(String errorMessage, Throwable originalException) {
        super(errorMessage, originalException);
    }

    public BadUsernameException(String errorMessage) {
        super(errorMessage);
    }
}

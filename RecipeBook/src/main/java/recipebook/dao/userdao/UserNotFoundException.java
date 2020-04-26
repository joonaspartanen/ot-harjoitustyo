package recipebook.dao.userdao;

public class UserNotFoundException extends Exception {

    private static final long serialVersionUID = -1096606910660644477L;

    public UserNotFoundException(String errorMessage, Throwable originalException) {
        super(errorMessage, originalException);
    }

    public UserNotFoundException(String errorMessage) {
        super(errorMessage);
    }
}
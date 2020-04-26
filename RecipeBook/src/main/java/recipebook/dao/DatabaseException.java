package recipebook.dao;

public class DatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    public DatabaseException(String errorMessage, Throwable originalException) {
        super(errorMessage, originalException);
    }

}
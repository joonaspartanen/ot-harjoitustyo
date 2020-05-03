package recipebook.dao;

/**
 * SQLExpections are wrapped into the DatabaseException. DatabaseExceptions are
 * caught in the Ui and used to show descriptive error messages.
 */
public class DatabaseException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     * 
     * @param errorMessage      Descriptive error message
     * @param originalException The original exception is wrapped into
     *                          DatabaseException in order to conserve the original
     *                          stack trace for debugging purposes.
     */
    public DatabaseException(String errorMessage, Throwable originalException) {
        super(errorMessage, originalException);
    }

}
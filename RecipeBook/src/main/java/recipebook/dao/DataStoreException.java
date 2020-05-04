package recipebook.dao;

/**
 * SQLExpections are wrapped into the DataStoreException. DataStoreExceptions
 * are caught in the UI and used to show descriptive error messages.
 */
public class DataStoreException extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor
     *
     * @param errorMessage Descriptive error message
     * @param originalException The original exception is wrapped into
     * DataStoreException in order to conserve the original stack trace for
     * debugging purposes.
     */
    public DataStoreException(String errorMessage, Throwable originalException) {
        super(errorMessage, originalException);
    }

    /**
     * Constructor for cases where no other exception is wrapped into the
     * DataStoreException.
     *
     * @param errorMessage Descriptive error message.
     */
    public DataStoreException(String errorMessage) {
        super(errorMessage);
    }

}

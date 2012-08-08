package accordion;

/**
 * Service exceptions represent resource failures and generally non-state
 * related errors.
 *
 * @author cputnam
 */
public class ServiceException extends BaseException {
    private static final long serialVersionUID = 1L;

    /**
     * Default pass through.
     */
    public ServiceException() {
        super();

    }

    /**
     * Pass through to parent.
     *
     * @param message
     * @param cause
     * @param errorCode
     * @param errorMessage
     */
    public ServiceException(String message, Throwable cause, String errorCode, String errorMessage) {
        super(message, cause, errorCode, errorMessage);

    }

    /**
     * Pass through to parent.
     *
     * @param message
     * @param cause
     */
    public ServiceException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * Pass through to parent.
     *
     * @param message
     */
    public ServiceException(String message) {
        super(message);

    }

    public ServiceException(String message, String errorCode) {
        super(message, null, errorCode);

    }

    /**
     * Pass through to parent.
     *
     * @param cause
     */
    public ServiceException(Throwable cause) {
		super(cause);

	}

}
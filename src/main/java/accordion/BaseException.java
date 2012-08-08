package accordion;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for exceptions. If an underlying throwable is provided, and it is
 * an instance of BaseException, the errorCode and errorMessage from the
 * underlying throwable is used.
 * 
 * @author cputnam
 * 
 */
public abstract class BaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Default error code used when creating exceptions.
	 */
	public static final String DEFAULT_ERROR_CODE = ExceptionCodes.INTERNAL_SERVICE_ERROR;

	/**
	 * Default error message used when creating exceptions.
	 */
	public static final String DEFAULT_ERROR_MESSAGE = "An internal error occurred.";

	protected String errorCode;

	protected String errorMessage;

	protected List messages;

	/**
	 * Default constructor.
	 */
	public BaseException() {
		this(null, null, null, null, null);
	}

	/**
	 * Default error code and attached messages.
	 * 
	 * @param message
	 * @param cause
	 */
	public BaseException(String message, Throwable cause) {
		this(message, cause, null, message, null);
	}

	/**
	 * Default cause, error code, and attached messages.
	 * 
	 * @param message
	 */
	public BaseException(String message) {
		this(message, null, null, message, null);
	}

	/**
	 * Default message, error code, and attached messages.
	 * 
	 * @param cause
	 */
	public BaseException(Throwable cause) {
		this(null, cause, null, null, null);
	}

	/**
	 * Default attached messages.
	 * 
	 * @param message
	 * @param cause
	 * @param errorCode
	 */
	public BaseException(String message, Throwable cause, String errorCode) {
		this(message, cause, errorCode, message, null);
	}

	/**
	 * Default attached messages. Differentiation between exception message and
	 * specific error message.
	 * 
	 * @param message
	 * @param cause
	 * @param errorCode
	 * @param errorMessage
	 */
	public BaseException(String message, Throwable cause, String errorCode, String errorMessage) {
		this(message, cause, errorCode, errorMessage, null);
	}

	/**
	 * Error message same as exception message.
	 * 
	 * @param message
	 * @param cause
	 * @param errorCode
	 * @param messages
	 */
	public BaseException(String message, Throwable cause, String errorCode, List messages) {
		this(message, cause, errorCode, message, messages);
	}

	/**
	 * Accepts all variations.
	 * 
	 * @param message
	 * @param cause
	 * @param errorCode
	 * @param errorMessage
	 * @param messages
	 */
	public BaseException(String message, Throwable cause, String errorCode, String errorMessage, List messages) {
		super(message, cause);
		if (messages == null) {
			messages = new ArrayList();
		}
		this.messages = messages;
		if (cause != null && cause instanceof BaseException) {
			this.errorCode = ((BaseException) cause).getErrorCode();
			this.errorMessage = ((BaseException) cause).getErrorMessage();
		} else {
			this.errorCode = (errorCode == null)
					? DEFAULT_ERROR_CODE
					: errorCode;
			this.errorMessage = (errorMessage == null)
					? DEFAULT_ERROR_MESSAGE
					: errorMessage;
		}
	}

	/**
	 * 
	 * @return error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * @return errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return messages
	 */
	public List getMessages() {
		return messages;
	}

	/**
	 * @param messages
	 */
	public void setMessages(List messages) {
		this.messages = messages;
	}

}

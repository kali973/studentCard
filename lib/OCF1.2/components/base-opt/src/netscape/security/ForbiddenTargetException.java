package lib.OCF1;

import java.lang.RuntimeException;

/**
 * This exception is thrown when a privilege request is denied.
 * @see netscape.security.PrivilegeManager
 */
public
class ForbiddenTargetException extends RuntimeException {
    /**
     * Constructs an IllegalArgumentException with no detail message.
     * A detail message is a String that describes this particular exception.
     */
    public ForbiddenTargetException() {
	super();
    }

    /**
     * Constructs an ForbiddenTargetException with the specified detail message.
     * A detail message is a String that describes this particular exception.
     * @param s the detail message
     */
    public ForbiddenTargetException(String s) {
	super(s);
    }
}


package xyz.jbapps.vplan.util;

/**
 * @author Jannik Bamberger (dev.jbamberger@gmail.com)
 */

public class ParseException extends Exception {
    public ParseException() {
        super();
    }

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }
}

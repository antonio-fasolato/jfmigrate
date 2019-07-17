package net.fasolato.jfmigrate;

/**
 * Exception returned by JFMigrate methods
 */
public class JFException extends RuntimeException {
    public JFException() {
    }

    public JFException(String s) {
        super(s);
    }

    public JFException(String s, Throwable throwable) {
        super(s, throwable);
    }
}

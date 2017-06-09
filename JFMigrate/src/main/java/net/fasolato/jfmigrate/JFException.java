package net.fasolato.jfmigrate;

/**
 * Created by fasolato on 04/04/2017.
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

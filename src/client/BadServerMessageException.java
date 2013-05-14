package client;

/**
 * Wrapper around RuntimeException for errors which are due to an
 * incomprehensible message from the server.
 */
public class BadServerMessageException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public BadServerMessageException() {
        super();
    }

    public BadServerMessageException(String s) {
        super(s);
    }
}

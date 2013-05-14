package client;

/**
 * Wrapper for Exception for errors which are due to an
 * incomprehensible message from the server.
 */
public class BadServerMessageException extends Exception {

	private static final long serialVersionUID = -2000467608902561751L;

	public BadServerMessageException() {
        super();
    }

    public BadServerMessageException(String s) {
        super(s);
    }
}

package client;

public class BadServerMessageException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public BadServerMessageException() {
        super();
    }
    
    public BadServerMessageException(String s) {
        super(s);
    }
}

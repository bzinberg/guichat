package client;

/**
 * Represents a message to the server.
 * 
 * TODO write a real Javadoc
 */
public class MessageToServer {
    private String messageText;
    private volatile boolean cancelled;

    public MessageToServer(String messageText) {
        this.messageText = messageText;
        this.cancelled = false;
    }

    public MessageToServer(String messageText, boolean cancelled) {
        this.messageText = messageText;
        this.cancelled = cancelled;
    }
    
    public String getMessageText() {
        return this.messageText;
    }
    
    public void cancel() {
        this.cancelled = true;
    }
}

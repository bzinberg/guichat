package client;

/**
 * Represents a message to the server.
 * 
 * TODO write a real Javadoc
 */
public class DefaultMessageToServer implements MessageToServer {
    private String messageText;
    private volatile boolean cancelled;

    public DefaultMessageToServer(String messageText) {
        this.messageText = messageText;
        this.cancelled = false;
    }

    public DefaultMessageToServer(String messageText, boolean cancelled) {
        this.messageText = messageText;
        this.cancelled = cancelled;
    }

    @Override
    public String getMessageText() {
        return this.messageText;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }
}

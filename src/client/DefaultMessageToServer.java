package client;

/**
 * Simple default implementation of MessageToServer.
 */
public class DefaultMessageToServer implements MessageToServer {
    private String messageText;
    private volatile boolean canceled;

    public DefaultMessageToServer(String messageText) {
        this.messageText = messageText;
        this.canceled = false;
    }

    public DefaultMessageToServer(String messageText, boolean canceled) {
        this.messageText = messageText;
        this.canceled = canceled;
    }

    @Override
    public String getMessageText() {
        return this.messageText;
    }

    @Override
    public boolean isCanceled() {
        return this.canceled;
    }

    @Override
    public void cancel() {
        this.canceled = true;
    }
}

package client;

/**
 * Interface for messages to the server. Each message must have a text body and
 * must be cancellable. When it comes time to send a given message to the
 * server, the OutgoingMessageManager will only send it if it has not yet been
 * cancelled.
 */
public interface MessageToServer {

    /**
     * Returns the text body of the message.
     */
    public abstract String getMessageText();

    /**
     * Checks whether the message has been cancelled.
     */
    public abstract boolean isCancelled();

    /**
     * Cancels the message.
     */
    public abstract void cancel();

}
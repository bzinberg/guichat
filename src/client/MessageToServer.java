package client;

/**
 * Interface for messages to the server. Each message must have a text body and
 * must be cancel-able. When it comes time to send a given message to the
 * server, the OutgoingMessageManager will only send it if it has not yet been
 * cancelled.
 */
public interface MessageToServer {

    /**
     * Returns the text of the message, according to the network protocol grammar.
     */
    public String getMessageText();

    /**
     * Checks whether the message has been canceled.
     */
    public boolean isCanceled();

    /**
     * Cancels the message.
     */
    public void cancel();

}
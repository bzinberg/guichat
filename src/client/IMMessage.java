package client;

/**
 * Represents an IM message. Has a data field to indicate whether it is pending.
 * Implements MessageToServer, so <tt>IMMessage</tt>s can be cancelled.
 */
public class IMMessage implements MessageToServer {
    private final String user;
    private final String message;
    private final String convName;
    private final boolean pending;
    private final int messageID;
    private volatile boolean cancelled;

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public String getConvName() {
        return convName;
    }

    public boolean isPending() {
        return pending;
    }

    public int getMessageID() {
        return messageID;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    /*
     * The sense in which this message "gets message text" is described in the
     * Javadoc for getMessageText() in the interface MessageToServer.
     */
    @Override
    public String getMessageText() {
        return "1" + "\t" + convName + "\t" + messageID + "\t" + message;
    }

    /**
     * Constructor for IMMessage.
     * 
     * @param _user
     *            Name of user who sent the message
     * @param _message
     *            Message text
     * @param _convName
     *            Name of conversation to which the message is to be sent
     * @param _pending
     *            Whether the message is at the "pending" stage
     * @param _uniqueID
     *            Message ID (see design doc)
     */
    public IMMessage(String _user, String _message, String _convName,
            boolean _pending, int _uniqueID) {
        user = _user;
        message = _message;
        convName = _convName;
        pending = _pending;
        messageID = _uniqueID;
        cancelled = false;
    }

}

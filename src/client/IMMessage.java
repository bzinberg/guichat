package client;

import network.NetworkConstants;

/**
 * Represents an IM message. Has a data field to indicate whether it is pending.
 * Implements MessageToServer, so <tt>IMMessage</tt>s can be cancelled.
 */
public class IMMessage implements MessageToServer {
    private final String username;
    private final String message;
    private final String convName;
    private final boolean pending;
    private final int messageId;
    private volatile boolean canceled;

    /**
     * Accessor method for username.
     * 
     * @return username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Accessor method for message.
     * 
     * @return message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Accessor method for convName.
     * 
     * @return convName.
     */
    public String getConvName() {
        return convName;
    }

    /**
     * Returns whether or not this message is pending.  True if this.pending is
     * true; false otherwise.
     * 
     * @return True if this message is pending; false otherwise.
     */
    public boolean isPending() {
        return pending;
    }

    /**
     * Accessor method for messageId.
     * 
     * @return messageId.
     */
    public int getMessageId() {
        return messageId;
    }

    public void cancel() {
    	canceled = true;
    }


    public boolean isCanceled() {
        return canceled;
    }

    /*
     * The sense in which this message "gets message text" is described in the
     * Javadoc for getMessageText() in the interface MessageToServer.
     */
    @Override
    public String getMessageText() {
        return NetworkConstants.IM + "\t" + convName + "\t" + messageId + "\t" + message;
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
     * @param _messageId
     *            Unique message ID (see design doc)
     */
    public IMMessage(String _user, String _message, String _convName,
            boolean _pending, int _messageId) {
        username = _user;
        message = _message;
        convName = _convName;
        pending = _pending;
        messageId = _messageId;
        canceled = false;
    }

}

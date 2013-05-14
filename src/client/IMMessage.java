package client;

import javax.swing.text.BadLocationException;

public class IMMessage implements MessageToServer {
    private final String user;
    private final String message;
    private final String convName;
    private final boolean pending;
    private final int uniqueID;
    private volatile boolean cancelled;

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
    
    public boolean isPending() {
        return pending;
    }
    
    public int getUniqueID() {
        return uniqueID;
    }
    
    @Override
    public void cancel() {
        cancelled = true;
    }
    
    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    
    @Override
    public String getMessageText() {
        return "1" + "\t" + convName + "\t" + uniqueID + "\t" + message;
    }

    public IMMessage(String _user, String _message, String _convName, boolean _pending, int _uniqueID) {
        user = _user;
        message = _message;
        convName = _convName;
        pending = _pending;
        uniqueID = _uniqueID;
        cancelled = false;
    }
    
}

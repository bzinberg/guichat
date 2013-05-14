package client;

import javax.swing.text.BadLocationException;

public class IMMessage {
    private final String user;
    private final String message;
    private final boolean pending;
    private final int uniqueID;

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

    public IMMessage(String _user, String _message, boolean _pending, int _uniqueID) {
        user = _user;
        message = _message;
        pending = _pending;
        uniqueID = _uniqueID;
    }
}

package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;

/**
 * Stores the history of received messages (not including pending messages).
 */
public class ConversationHistory {
    private final Map<String, List<IMMessage>> history;
    private final DefaultListModel listModel;
    private final String myUsername;

    public ConversationHistory(DefaultListModel _listModel, String _myUsername) {
        listModel = _listModel;
        myUsername = _myUsername;

        // TreeMap because we want the relative ordering of conversations in the
        // history-view GUI to be persistent under inserting new conversations
        history = new TreeMap<String, List<IMMessage>>();
    }

    /**
     * Logs a new message in history. Only received messages should be logged,
     * not pending messages.
     * 
     * If the conversation to which this message belongs is not already in
     * this.history, adds convName as a row in this.listModel, to display it
     * in the past conversations table in the top level panel of the GUI.
     */
    public void logNew(IMMessage message) {
        String convName = message.getConvName();
        if (!history.containsKey(convName)) {
            history.put(convName, new ArrayList<IMMessage>());
            listModel.addElement(convName);
        }

        history.get(convName).add(message);
    }

    /**
     * Create a MessagesDoc to display the history of the conversation with the
     * given name. Requires that the conversation name be in this.history.
     * 
     * @param convName
     *            Name of the conversation
     * @return a MessageDoc for displaying the conversation history
     */
    public MessagesDoc historyDocument(String convName) {
        MessagesDoc doc = new MessagesDoc(myUsername, convName);
        List<IMMessage> messages = history.get(convName);
        for (IMMessage m : messages) {
            doc.receiveMessage(m);
        }
        return doc;
    }
}

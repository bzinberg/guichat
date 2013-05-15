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
     * Log a new message in history. Only received messages should be logged,
     * not pending messages.
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
     * given name.
     * 
     * @param conv
     *            Name of the conversation
     * @return a MessageDoc for displaying the conversation history
     */
    public MessagesDoc historyDocument(String conv) {
        MessagesDoc doc = new MessagesDoc(myUsername, conv);
        List<IMMessage> messages = history.get(conv);
        for (IMMessage m : messages) {
            doc.receiveMessage(m);
        }
        return doc;
    }
}

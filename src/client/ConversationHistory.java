package client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.DefaultListModel;

public class ConversationHistory {
    private final Map<String, List<IMMessage>> history;
    private final DefaultListModel listModel;
    private final String myUsername;
    
    public ConversationHistory(DefaultListModel _listModel, String _myUsername) {
        listModel = _listModel;
        myUsername = _myUsername;
        
        history = new TreeMap<String, List<IMMessage>>();
    }
    
    public void logNew(IMMessage message) {
        String convName = message.getConvName();
        if(!history.containsKey(convName)) {
            history.put(convName, new ArrayList<IMMessage>());
            listModel.addElement(convName);
        }
        
        history.get(convName).add(message);
    }
    
    public MessagesDoc historyDocument(String conv) {
        MessagesDoc doc = new MessagesDoc(myUsername, conv);
        List<IMMessage> messages = history.get(conv);
        for(IMMessage m: messages) {
            doc.receiveMessage(m);
        }
        return doc;
    }
}

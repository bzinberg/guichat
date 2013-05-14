package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class ClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final BufferedReader in;
    private final PrintWriter out;

    private final String serverName;
    private final String myUsername;

    private final Set<String> otherUsersSet;

    private final JTabbedPane tabbedPane;
    private final TopLevelPanel topLevelPanel;
    private final JPanel statusPanel;
    private final JLabel status;

    private final Map<String, ConversationPanel> conversations;

    private final IncomingMessageManager incomingMessageManager;

    public ClientGUI(BufferedReader _in, PrintWriter _out, String _serverName,
            String _myUsername, String initUserList) {
        in = _in;
        out = _out;
        serverName = _serverName;
        myUsername = _myUsername;

        otherUsersSet = new HashSet<String>();
        String[] initUsers = initUserList.split("\t", -1);
        for (String username : initUsers) {
            if (!username.equals(myUsername)) {
                otherUsersSet.add(username);
            }
        }

        conversations = new HashMap<String, ConversationPanel>();

        this.setTitle("Connected to " + serverName + " as " + myUsername);

        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        topLevelPanel = new TopLevelPanel();
        tabbedPane.addTab("(Top Level)", topLevelPanel);
        for (String username : otherUsersSet) {
            topLevelPanel.otherUsersModel.addElement(username);
        }

        /* Many thanks to krock on StackOverflow for this status bar idea */
        status = new JLabel();
        status.setName("status");
        statusPanel = new JPanel();
        statusPanel.setName("statusPanel");
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(status, BorderLayout.SOUTH);
        statusPanel.setMinimumSize(new Dimension(50, 20));
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        Container content = this.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(tabbedPane);
        content.add(statusPanel);

        this.setMinimumSize(new Dimension(600, 400));

        incomingMessageManager = new IncomingMessageManager(in, this);
        incomingMessageManager.start();
    }

    /**
     * TODO Javadoc
     * 
     * Makes the tab in this.tabbedPane with component conv have a close button.
     */
    private void addCloseButton(ConversationPanel conv) {
        int i = tabbedPane.indexOfComponent(conv);
        if (i == -1) {
            throw new RuntimeException(
                    "Tried to modify a tab that doesn't exist");
        } else {
            tabbedPane.setTabComponentAt(i, new ConversationTabComponent(
                    tabbedPane, conv));
        }
    }

    public void tryToEnterConv(String convName, String _otherUsers) {
        if (conversations.containsKey(convName)) {
            status.setText("Tried to enter conversation " + convName
                    + ", but we're already in it.");
            return;
        }
        
        String[] otherUsers = _otherUsers.split("\t", -1);

        ConversationPanel panel = new ConversationPanel(convName, myUsername);
        tabbedPane.add(convName, panel);
        addCloseButton(panel);
        conversations.put(convName, panel);

        for (String username : otherUsers) {
            if (!username.equals(myUsername)) {
                panel.otherUsersSet.add(username);
            }
        }

        for (String username : panel.otherUsersSet) {
            panel.otherUsersModel.addElement(username);
        }
    }

    public void tryToRemoveUserFromConv(String username, String convName) {
        if (!conversations.containsKey(convName)) {
            status.setText("Tried to remove user " + username
                    + " from conversation " + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }

        ConversationPanel panel = conversations.get(convName);

        if (panel.otherUsersSet.contains(username)) {
            panel.otherUsersSet.remove(username);
            int index = panel.otherUsersModel.indexOf(username);
            if (index != -1) {
                panel.otherUsersModel.remove(index);
            }
        } else {
            status.setText("Tried to remove user " + username
                    + " from conversation " + convName
                    + ", but we didn't think he was in it");
        }
    }

    public void tryToAddUserToConv(String username, String convName) {
        if (!conversations.containsKey(convName)) {
            status.setText("Tried to add user " + username
                    + " to conversation " + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }

        ConversationPanel panel = conversations.get(convName);

        if (panel.otherUsersSet.contains(username)) {
            status.setText("Tried to add user " + username
                    + " to conversation " + convName
                    + ", but we thought he was already in it");
        } else {
            panel.otherUsersSet.add(username);
            panel.otherUsersModel.addElement(username);
        }
    }

    public void handleConnectedMessage(String username) {
        if (otherUsersSet.contains(username)) {
            status.setText("Tried to register new user " + username
                    + ", but we thought he was already connected to server");
            return;
        }

        otherUsersSet.add(username);
        topLevelPanel.otherUsersModel.addElement(username);
    }

    public void handleDisconnectedMessage(String username) {
        if (!otherUsersSet.contains(username)) {
            status.setText("Tried to register disconnected user "
                    + username
                    + ", but we didn't think he was connected to the server in the first place");
            return;
        }

        otherUsersSet.remove(username);
        int index = topLevelPanel.otherUsersModel.indexOf(username);
        if (index != -1) {
            topLevelPanel.otherUsersModel.remove(index);
        }
    }

    public void handleParticipantsMessage(String convName, String users) {
        if (!conversations.containsKey(convName)) {
            status.setText("Tried to update participants list for conversation"
                    + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }

        ConversationPanel panel = conversations.get(convName);
        Set<String> newOtherUsersSet = new HashSet<String>();
        Set<String> toAdd = new HashSet<String>();
        String[] _users = users.split("\t", -1);
        for (String username : _users) {
            newOtherUsersSet.add(username);
            if (!panel.otherUsersSet.contains(username)) {
                toAdd.add(username);
            }
        }

        String elt;
        for (int i = 0; i < panel.otherUsersModel.size(); i++) {
            elt = (String) panel.otherUsersModel.get(i);
            if (!newOtherUsersSet.contains(elt)) {
                panel.otherUsersModel.remove(i);
            }
        }

        for (String username : toAdd) {
            panel.otherUsersModel.addElement(username);
        }

        panel.otherUsersSet = newOtherUsersSet;
    }

    public void handleErrorMessage(String rejectedInput) {
        status.setText("Server rejected the following message from us: "
                + rejectedInput);
    }

    public void registerIM(String username, String convName, String _uniqueID,
            String text) {
        if (!conversations.containsKey(convName)) {
            status.setText("Tried to receive message for conversation"
                    + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }
        
        ConversationPanel panel = conversations.get(convName);
        int uniqueID = Integer.parseInt(_uniqueID);
        IMMessage message = new IMMessage(username, text, false, uniqueID);
        panel.messagesDoc.receiveMessage(message);
    }
}

class ConversationCloseListener implements ActionListener {
    private final ConversationPanel conv;

    public ConversationCloseListener(ConversationPanel _conv) {
        conv = _conv;
    }

    public void actionPerformed(ActionEvent e) {
        conv.close();
    }
}
package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.BevelBorder;

import network.NetworkConstants;

/**
 * The main class for the IM Client. Underlies the main instant messenger window
 * and also contains most of the data and methods associated with an IM session.
 */
public class ClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    // How we communicate with the server
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;

    // Human-readable name of server
    private final String serverName;

    private final String myUsername;

    // Set of other users connected to the server
    private final Set<String> otherUsersSet;

    private final JTabbedPane tabbedPane;
    private final TopLevelPanel topLevelPanel;
    private final JPanel statusPanel;
    private final JLabel status;

    // Conversations we are involved in (maps conversation names to panels)
    private final Map<String, ConversationPanel> conversations;

    protected final ConversationHistory conversationHistory;

    private final IncomingMessageManager incomingMessageManager;
    protected final OutgoingMessageManager outgoingMessageManager;

    /**
     * Constructs an instance of ClientGUI.
     * 
     * @param _socket
     *            Socket to use for communication with server
     * @param _in
     *            BufferedReader to read messages from server (should be derived
     *            from _socket)
     * @param _out
     *            PrintWriter to send messages to server (should be derived from
     *            _socket)
     * @param _serverName
     *            Human-readable name of server
     * @param _myUsername
     *            Username of this client on the server
     * @param initUserList
     *            Tab-delimited list of other users connected to the server at
     *            time of initiation
     */
    public ClientGUI(Socket _socket, BufferedReader _in, PrintWriter _out,
            String _serverName, String _myUsername, String initUserList) {
        socket = _socket;
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

        topLevelPanel = new TopLevelPanel(this);
        tabbedPane.addTab("(Top Level)", topLevelPanel);
        for (String username : otherUsersSet) {
            topLevelPanel.otherUsersModel.addElement(username);
        }
        conversationHistory = new ConversationHistory(
                topLevelPanel.pastConversationsModel, myUsername);

        /* Many thanks to krock on StackOverflow for this status bar idea */
        status = new JLabel();
        status.setName("status");
        // So that the text field doesn't collapse
        setStatusText(" ");
        statusPanel = new JPanel();
        statusPanel.setName("statusPanel");
        statusPanel.setLayout(new BorderLayout());
        statusPanel.add(status, BorderLayout.SOUTH);
        statusPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));

        Container content = this.getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
        content.add(tabbedPane);
        content.add(statusPanel);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(700, 400));

        incomingMessageManager = new IncomingMessageManager(in, this);
        incomingMessageManager.start();

        outgoingMessageManager = new OutgoingMessageManager(out);
        outgoingMessageManager.start();
    }

    /**
     * Makes the tab in this.tabbedPane with component conv have a close button.
     * 
     * @param conv
     *            ConversationPanel to give a close button to.
     */
    private void addCloseButton(ConversationPanel conv) {
    	int i = tabbedPane.indexOfComponent(conv);
    	tabbedPane.setTabComponentAt(i, new ConversationTabComponent(
    			tabbedPane, conv));
    }

    /**
     * Removes convName from the collection of names of conversations that we
     * think we're involved in. (Does not perform any GUI modifications.)
     * 
     * @param convName
     *            Name of conversation to remove
     */
    void removeConversationFromMap(String convName) {
    	conversations.remove(convName);
    }

    /**
     * Opens a new tab corresponding to the given conversation unless we already
     * have a tab open for it.
     * 
     * @param convName
     *            Name of new conversation
     * @param _otherUsers
     *            Tab-delimited list of other users in the conversation at time
     *            of initiation
     */
    void tryToEnterConv(String convName, String _otherUsers) {
        if (conversations.containsKey(convName)) {
            setStatusText("Tried to enter conversation " + convName
                    + ", but we're already in it.");
            return;
        }

        String[] otherUsers = _otherUsers.split("\t", -1);

        ConversationPanel panel = new ConversationPanel(convName, myUsername,
                this);
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

        tabbedPane.setSelectedComponent(panel);
    }

    /**
     * Removes the given user from our list of participants in the given
     * conversation, if we actually thought the user was in the conversation in
     * the first place.
     * 
     * @param username
     *            Username to remove
     * @param convName
     *            Convesration to remove him from
     */
    void tryToRemoveUserFromConv(String username, String convName) {
        if (!conversations.containsKey(convName)) {
            setStatusText("Tried to remove user " + username
                    + " from conversation " + convName
                    + ", but we are not in " + convName);
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
            setStatusText("Tried to remove user " + username
                    + " from conversation " + convName
                    + ", but " + username + " is not in "
                    + convName);
        }
    }

    /**
     * Adds the given user to our list of participants in the given
     * conversation, if we didn't already think the user was in the
     * conversation.
     * 
     * @param username
     *            Username to add
     * @param convName
     *            Conversation to add him to
     */
    void tryToAddUserToConv(String username, String convName) {
        if (!conversations.containsKey(convName)) {
            setStatusText("Tried to add user " + username
                    + " to conversation " + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }

        ConversationPanel panel = conversations.get(convName);

        if (panel.otherUsersSet.contains(username)) {
            setStatusText("Tried to add user " + username
                    + " to conversation " + convName
                    + ", but we thought he was already in it");
        } else {
            panel.otherUsersSet.add(username);
            panel.otherUsersModel.addElement(username);
        }
    }

    /**
     * Adds the given user to our list of other users connected to the server,
     * if we didn't already think the user was connected.
     * 
     * @param username
     *            Username to add
     */
    void handleConnectedMessage(String username) {
        if (otherUsersSet.contains(username)) {
            setStatusText("Tried to register new user " + username
                    + ", but we thought he was already connected to server");
            return;
        }

        otherUsersSet.add(username);
        topLevelPanel.otherUsersModel.addElement(username);
    }

    /**
     * Removes the given user from our list other users connected to the server,
     * if we actually thought the user was connected in the first place.
     * 
     * @param username
     *            Username to remove
     */
    void handleDisconnectedMessage(String username) {
        if (!otherUsersSet.contains(username)) {
            setStatusText("Tried to register disconnected user "
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

    /**
     * Handles a PARTICIPANTS message from the server, which tells us which
     * users are participating in a given conversation. We already have some
     * idea of which users we think are participating; this message serves as a
     * correction to compensate for issues of network latency.
     * 
     * @param convName
     *            The conversation to update
     * @param users
     *            Tab-delimited list of users in the conversation
     */
    void handleParticipantsMessage(String convName, String users) {
        if (!conversations.containsKey(convName)) {
            setStatusText("Tried to update participants list for conversation "
                    + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }

        ConversationPanel panel = conversations.get(convName);
        Set<String> newOtherUsersSet = new HashSet<String>();
        // Names we will have to add to our list of users
        // (this requires changing GUI elements, so I'd like
        // to require as few additions/removals as possible)
        Set<String> toAdd = new HashSet<String>();

        String[] _users = users.split("\t", -1);
        for (String username : _users) {
            newOtherUsersSet.add(username);
            if (!panel.otherUsersSet.contains(username)) {
                toAdd.add(username);
            }
        }

        // Perform the necessary removals from the GUI
        String elt;
        for (int i = 0; i < panel.otherUsersModel.size(); i++) {
            elt = (String) panel.otherUsersModel.get(i);
            if (!newOtherUsersSet.contains(elt)) {
                panel.otherUsersModel.remove(i);
            }
        }

        // Perform the necessary additions to the GUI
        for (String username : toAdd) {
            panel.otherUsersModel.addElement(username);
        }

        // Update panel.otherUsersSet
        panel.otherUsersSet = newOtherUsersSet;
    }

    /**
     * Handles an ERROR message from the server, which says that it didn't like
     * our previous message
     * 
     * @param rejectedInput
     *            The bad previous message to server
     */
    void handleErrorMessage(String rejectedInput) {
        setStatusText("Server rejected the following message from us: "
                + rejectedInput);
    }

    /**
     * Takes in data about an IM message as received from the server and
     * processes the message, updating the GUI and logging in history as
     * appropriate.
     * 
     * @param username
     *            Username of sender of the message
     * @param convName
     *            Name of conversation in which the message occurred
     * @param _messageId
     *            Message ID (see design doc)
     * @param text
     *            Text of the message
     */
    void registerIM(String username, String convName, String _messageId,
            String text) {
        if (!conversations.containsKey(convName)) {
            setStatusText("Tried to receive message for conversation"
                    + convName
                    + ", but we didn't think we were in that conversation");
            return;
        }

        ConversationPanel panel = conversations.get(convName);
        int messageId = Integer.parseInt(_messageId);
        IMMessage message = new IMMessage(username, text, convName, false,
                messageId);
        panel.messagesDoc.receiveMessage(message);
        conversationHistory.logNew(message);

        // If we didn't think this user was in the conversation, refresh our
        // list of participants
        if (!username.equals(myUsername)
                && !panel.otherUsersSet.contains(username)) {
            String content = NetworkConstants.RETRIEVE_PARTICIPANTS + "\t" + convName;
            outgoingMessageManager.add(new DefaultMessageToServer(content));
        }
    }

    /**
     * Prompts the user to enter a new conversation.
     */
    void promptForNewRoom() {
        String name = (String) JOptionPane
                .showInputDialog("Enter desired name of the new conversation and we will try to make it.\nIf you give an empty name then the server will autogenerate a name for you.");
        if (name == null) {
            // user closed the prompt
            return;
        }
        if (name.length() > 256 || name.contains("\t") || name.contains("\n")) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "Conversation name must be at most 256 characters and cannot contain tabs or newlines.");
            return;
        }
        String messageContent = NetworkConstants.NEW_CONV + "\t" + name;
        outgoingMessageManager.add(new DefaultMessageToServer(messageContent));
    }

    /**
     * Prompts the user to start a new two-way conversation.
     */
    void promptForTwoWayConv() {
        String username = (String) JOptionPane
                .showInputDialog("Enter name of other participant and we will try to make a new two-way conversation:");
        if (username == null) {
            // user closed the prompt
            return;
        }
        if (username.isEmpty() || username.length() > 256
                || username.contains("\t") || username.contains("\n")) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "Username must be nonempty, at most 256 characters, and cannot contain tabs or newlines.");
            return;
        }
        String messageContent = NetworkConstants.TWO_WAY_CONV + "\t" + username;
        outgoingMessageManager.add(new DefaultMessageToServer(messageContent));
    }

    /**
     * Prompts the user to choose a conversation to join.
     */
    void promptToJoinConv() {
        String name = (String) JOptionPane
                .showInputDialog("Enter the name of a conversation and we will try to join it:");
        if (name == null) {
            // user closed the prompt
            return;
        }
        if (name.isEmpty() || name.length() > 256 || name.contains("\t")
                || name.contains("\n")) {
            JOptionPane
                    .showMessageDialog(
                            this,
                            "Conversation name must be nonempty, at most 256 characters, and cannot contain tabs or newlines.");
            return;
        }
        String messageContent = NetworkConstants.ENTER_CONV + "\t" + name;
        outgoingMessageManager.add(new DefaultMessageToServer(messageContent));
    }

    /**
     * Ceases communication with server.
     */
    void disconnect() {
        incomingMessageManager.stop();
        outgoingMessageManager.stop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Sets the text of the status bar.
     * 
     * Must be called from the event dispatch thread.
     * 
     * @param s The new status string, non-null.
     */
    void setStatusText(String s) {
    	status.setText(s);
    }
}

/**
 * Listener that closes the given conversation (not just its GUI panel) upon
 * activation.
 */
class ConversationCloseListener implements ActionListener {
    private final ConversationPanel conv;

    public ConversationCloseListener(ConversationPanel _conv) {
        conv = _conv;
    }

    public void actionPerformed(ActionEvent e) {
        conv.close();
    }
}

/**
 * Listener that disconnects and restarts the IM client upon activation.
 */
class DisconnectListener implements ActionListener {
    private final ClientGUI clientGUI;

    public DisconnectListener(ClientGUI _clientGUI) {
        clientGUI = _clientGUI;
    }

    public void actionPerformed(ActionEvent e) {
        clientGUI.disconnect();
        clientGUI.dispose();

        ConnectWindow connectWindow = new ConnectWindow();
        connectWindow.setVisible(true);
    }
}
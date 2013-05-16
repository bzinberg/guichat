package client;

import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

import network.NetworkConstants;

import java.awt.event.*;

/**
 * Panel displaying a single conversation. In the client GUI, there will be a
 * ConversationPanel attached to a tab for each conversation.
 */
public class ConversationPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ClientGUI clientGUI;

    private final JLabel otherUsersHeading;
    protected final DefaultListModel otherUsersModel;
    private final JList otherUsers;
    private final JScrollPane otherUsersScrollPane;
    private final JLabel inviteHeading;
    private final JTextField inviteField;
    private final JButton inviteButton;
    private final JTextPane messages;
    private final JScrollPane messagesScrollPane;
    private final JTextField newMessage;
    private final JButton sendButton;

    private final String myUsername;
    private final String convName;
    protected final MessagesDoc messagesDoc;

    /** Unique messageId for our outgoing messages (see design doc) */
    private int messageId;

    /**
     * Names of other users. The names of other users are already stored in the
     * JList otherUsers, but we include this object alongside it in order to
     * increase efficiency (constant time rather than linear time for some of
     * the lookup operations).
     */
    protected Set<String> otherUsersSet;

    /**
     * Constructor for ConversationPanel.
     * 
     * @param _convName
     *            Name of the conversation
     * @param _myUsername
     *            Name that the user has registered with the server
     * @param _clientGUI
     *            The parent instance of ClientGUI
     */
    public ConversationPanel(String _convName, String _myUsername,
            ClientGUI _clientGUI) {
        convName = _convName;
        myUsername = _myUsername;
        clientGUI = _clientGUI;

        // Initialize messageId to zero
        messageId = 0;

        otherUsersSet = new HashSet<String>();

        otherUsersHeading = new JLabel("Other Users");
        otherUsersHeading.setName("otherUsersHeading");

        otherUsersModel = new DefaultListModel();

        otherUsers = new JList(otherUsersModel);
        otherUsers.setName("otherUsers");
        otherUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        otherUsers.addMouseListener(new DoubleClickUsernameListener(clientGUI));

        otherUsersScrollPane = new JScrollPane(otherUsers);
        otherUsersScrollPane.setName("otherUsersScrollPane");

        inviteHeading = new JLabel("Invite User");
        inviteHeading.setName("inviteHeading");

        inviteField = new JTextField();
        inviteField.setName("inviteField");
        inviteField.addActionListener(new InviteListener(this));
        inviteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        inviteButton = new JButton("Invite");
        inviteButton.setName("inviteButton");
        inviteButton.addActionListener(new InviteListener(this));

        messages = new JTextPane();
        messages.setName("messages");
        messages.setEditable(false);

        messagesDoc = new MessagesDoc(myUsername, convName);
        messages.setDocument(messagesDoc);

        messagesScrollPane = new JScrollPane(messages);
        messagesScrollPane.setName("messagesScrollPane");

        newMessage = new JTextField();
        newMessage.setName("newMessage");
        newMessage.addActionListener(new SendIMListener(this));
        newMessage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        sendButton = new JButton("Send");
        sendButton.setName("sendButton");
        sendButton.addActionListener(new SendIMListener(this));

        /*
         * Layout:
         * 
         * --------------------------------
         * 
         * Other users: |.. TEXT DISPLAY ..|
         * 
         * .............|..................|
         * 
         * .............|..................|
         * 
         * .............|..................|
         * 
         * Invite user: |..................|
         * 
         * |===========||..................|
         * 
         * |..|Invite|..|============|Send||
         * 
         * ---------------------------------
         */
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.PAGE_AXIS));
        leftColumn.add(otherUsersHeading);
        leftColumn.add(otherUsersScrollPane);
        leftColumn.add(inviteHeading);
        leftColumn.add(inviteField);
        leftColumn.add(inviteButton);
        leftColumn.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout
                .createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(leftColumn))
                .addGroup(
                        layout.createParallelGroup()
                                .addComponent(messagesScrollPane)
                                .addGroup(
                                        layout.createSequentialGroup()
                                                .addComponent(newMessage)
                                                .addComponent(sendButton))));
        layout.setVerticalGroup(layout
                .createParallelGroup()
                .addGroup(
                        layout.createSequentialGroup().addComponent(leftColumn))
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(messagesScrollPane)
                                .addGroup(
                                        layout.createParallelGroup()
                                                .addComponent(newMessage)
                                                .addComponent(sendButton))));

        this.setLayout(layout);
    }

    /**
     * Cancels all pending messages in this conversation, removes this
     * conversation from our collection of ongoing conversations, and sends an
     * EXIT_CONV message to server to let it know that we are leaving the
     * conversation.
     */
    public void close() {
        for (IMMessage message : messagesDoc.pending.values()) {
            message.cancel();
        }

        String content = NetworkConstants.EXIT_CONV + "\t" + convName;
        clientGUI.outgoingMessageManager
                .add(new DefaultMessageToServer(content));
        clientGUI.removeConversation(convName);
    }

    /**
     * Invites the user whose name is in the "invite" field to this
     * conversation. Complains if the contents of the field are invalid. Upon
     * successfully sending the message, clears the contents of the invite
     * field.
     */
    public void createInviteMessage() {
        String invitee = inviteField.getText();
        if (invitee.isEmpty() || invitee.contains("\t")) {
            JOptionPane
                    .showMessageDialog(clientGUI,
                            "Invitee's username must be nonempty and cannot contain tab characters.");
            return;
        }

        String content = NetworkConstants.ADD_TO_CONV + "\t" + invitee + "\t"
                + convName;
        clientGUI.outgoingMessageManager
                .add(new DefaultMessageToServer(content));
        inviteField.setText("");
    }

    /**
     * Sends out an IM message according to the user input. Complains if the
     * contents of the message field are invalid. If the contents are valid,
     * registers the message as pending and increments messageId.
     */
    public void createIMMessage() {
        String messageText = newMessage.getText();
        if (messageText.isEmpty() || messageText.contains("\t")
                || messageText.contains("\n") || messageText.length() > 512) {
            JOptionPane.showMessageDialog(clientGUI,
                    "Your message must be nonempty and at most 512 characters"
                            + " and may not contain newlines or tabs.");
            return;
        }

        IMMessage message = new IMMessage(myUsername, messageText, convName,
                true, messageId);
        messagesDoc.receiveMessage(message);

        clientGUI.outgoingMessageManager.add(message);

        messageId++;
        newMessage.setText("");
    }
}

/**
 * Listener to listen for user clicking the Invite button or pressing enter in
 * the "invite" field.
 */
class InviteListener implements ActionListener {
    private final ConversationPanel panel;

    public InviteListener(ConversationPanel _panel) {
        panel = _panel;
    }

    public void actionPerformed(ActionEvent e) {
        panel.createInviteMessage();
    }
}

/**
 * Listener to listen for user clicking the Send button or pressing enter in the
 * message field.
 */
class SendIMListener implements ActionListener {
    private final ConversationPanel panel;

    public SendIMListener(ConversationPanel _panel) {
        panel = _panel;
    }

    public void actionPerformed(ActionEvent e) {
        panel.createIMMessage();
    }
}

/**
 * Listener to listen for user double-clicking a list of users (be it a list of
 * other participants in the conversation or a list of other users connected to
 * the server at top level). Invites the clicked user to a two-way conversation.
 */
class DoubleClickUsernameListener extends MouseAdapter {
    /*
     * Many thanks to Mohamed Saligh on StackOverflow for explaining how to
     * listen for double clicks
     */
    private final ClientGUI clientGUI;

    public DoubleClickUsernameListener(ClientGUI _clientGUI) {
        clientGUI = _clientGUI;
    }

    public void mouseClicked(MouseEvent e) {
        JList list = (JList) e.getSource();
        if (e.getClickCount() == 2) {
            int index = list.locationToIndex(e.getPoint());
            String username = (String) list.getModel().getElementAt(index);
            // Request a new two-way conversation
            String content = NetworkConstants.TWO_WAY_CONV + "\t" + username;
            clientGUI.outgoingMessageManager.add(new DefaultMessageToServer(
                    content));
        }
    }
}
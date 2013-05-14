package client;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.*;
import javax.swing.text.*;

import java.awt.event.*;

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

    private int imID;

    protected Set<String> otherUsersSet;

    public ConversationPanel(String _convName, String _myUsername,
            ClientGUI _clientGUI) {
        convName = _convName;
        myUsername = _myUsername;
        clientGUI = _clientGUI;

        imID = 0;

        otherUsersSet = new HashSet<String>();

        otherUsersHeading = new JLabel("Other Users:");
        otherUsersHeading.setName("otherUsersHeading");

        otherUsersModel = new DefaultListModel();

        otherUsers = new JList(otherUsersModel);
        otherUsers.setName("otherUsers");
        otherUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        otherUsersScrollPane = new JScrollPane(otherUsers);
        otherUsersScrollPane.setName("otherUsersScrollPane");

        inviteHeading = new JLabel("Invite new user:");
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
         * TODO Comment briefly describing layout
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

    public void close() {
        for (IMMessage message : messagesDoc.pending.values()) {
            message.cancel();
        }

        String content = "5" + "\t" + convName;
        clientGUI.outgoingMessageManager
                .add(new DefaultMessageToServer(content));
        clientGUI.removeConversationFromMap(convName);
    }

    public void createInviteMessage() {
        String invitee = inviteField.getText();
        if (invitee.isEmpty() || invitee.contains("\t")) {
            JOptionPane
                    .showMessageDialog(clientGUI,
                            "Invitee's username must be nonempty and cannot contain tab characters.");
            return;
        }

        String content = "3" + "\t" + invitee + "\t" + convName;
        clientGUI.outgoingMessageManager
                .add(new DefaultMessageToServer(content));
        inviteField.setText("");
    }

    public void createIMMessage() {
        String messageText = newMessage.getText();
        if (messageText.isEmpty() || messageText.contains("\t")
                || messageText.contains("\n")) {
            JOptionPane
                    .showMessageDialog(clientGUI,
                            "Your message must be nonempty and cannot contain newlines or tabs.");
            return;
        }

        IMMessage message = new IMMessage(myUsername, messageText, convName,
                true, imID);
        messagesDoc.receiveMessage(message);
        
        /* TODO also check to make sure the message stays under 512 bytes */
        clientGUI.outgoingMessageManager.add(message);

        imID++;
        newMessage.setText("");
    }
}

class MessagesDoc extends DefaultStyledDocument {
    public Style regular;
    public Style bold;
    public Style grayItal;
    public Style boldGrayItal;

    private final String myUsername;
    private final String convName;

    /* Integers representing positions in the doc */
    private int endOfReceived;
    private int endOfPending;

    /** Maps a uniqueID to a message */
    protected Map<Integer, IMMessage> pending;

    public MessagesDoc(String _myUsername, String _convName) {
        myUsername = _myUsername;
        convName = _convName;

        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        regular = this.addStyle("regular", defaultStyle);
        bold = this.addStyle("bold", regular);
        StyleConstants.setBold(bold, true);
        grayItal = this.addStyle("grayItal", regular);
        StyleConstants.setForeground(grayItal, Color.GRAY);
        StyleConstants.setItalic(grayItal, true);
        boldGrayItal = this.addStyle("boldGrayItal", grayItal);
        StyleConstants.setBold(boldGrayItal, true);

        endOfReceived = 0;
        endOfPending = 0;

        pending = new TreeMap<Integer, IMMessage>();
    }

    public synchronized void addMessage(IMMessage m)
            throws BadLocationException {
        String byline = m.getUser() + ": ";
        String message = m.getMessage() + "\n";

        if (m.isPending()) {
            pending.put(m.getUniqueID(), m);
            this.insertString(endOfPending, byline, boldGrayItal);
            endOfPending += byline.length();
            this.insertString(endOfPending, message, grayItal);
            endOfPending += message.length();
        } else {
            this.insertString(endOfReceived, byline, bold);
            endOfReceived += byline.length();
            endOfPending += byline.length();
            this.insertString(endOfReceived, message, regular);
            endOfReceived += message.length();
            endOfPending += message.length();
        }
    }

    public synchronized void unpend(int uniqueID) throws BadLocationException {
        if (!pending.containsKey(uniqueID)) {
            throw new RuntimeException(
                    "Tried to unpend a message that doesn't exist");
        }

        IMMessage oldMessage = pending.get(uniqueID);
        pending.remove(uniqueID);
        IMMessage newMessage = new IMMessage(oldMessage.getUser(),
                oldMessage.getMessage(), convName, false, uniqueID);

        /* Rebuild the pending part of the message display */
        this.remove(endOfReceived, endOfPending - endOfReceived);
        endOfPending = endOfReceived;
        addMessage(newMessage);
        for (IMMessage m : pending.values()) {
            addMessage(m);
        }
    }

    public void receiveMessage(IMMessage message) {
        if (message.getUser().equals(myUsername)
                && pending.containsKey(message.getUniqueID())) {
            try {
                unpend(message.getUniqueID());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        } else {
            try {
                addMessage(message);
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }
}

class InviteListener implements ActionListener {
    private final ConversationPanel panel;

    public InviteListener(ConversationPanel _panel) {
        panel = _panel;
    }

    public void actionPerformed(ActionEvent e) {
        panel.createInviteMessage();
    }
}

class SendIMListener implements ActionListener {
    private final ConversationPanel panel;

    public SendIMListener(ConversationPanel _panel) {
        panel = _panel;
    }

    public void actionPerformed(ActionEvent e) {
        panel.createIMMessage();
    }
}
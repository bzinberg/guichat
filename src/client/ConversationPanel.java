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

public class ConversationPanel extends JPanel {
    private static final long serialVersionUID = 1L;

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

    protected Set<String> otherUsersSet;

    public ConversationPanel(String _convName, String _myUsername) {
        convName = _convName;
        myUsername = _myUsername;

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
        inviteField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));

        inviteButton = new JButton("Invite");
        inviteButton.setName("inviteButton");

        messages = new JTextPane();
        messages.setName("messages");
        messages.setEditable(false);

        messagesDoc = new MessagesDoc(myUsername);
        messages.setDocument(messagesDoc);

        messagesScrollPane = new JScrollPane(messages);
        messagesScrollPane.setName("messagesScrollPane");

        newMessage = new JTextField();
        newMessage.setName("newMessage");
        newMessage.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

        sendButton = new JButton("Send");
        sendButton.setName("sendButton");

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
        /* TODO Implement */
        System.out.println("Closed conversation");
    }
}

class MessagesDoc extends DefaultStyledDocument {
    public Style regular;
    public Style bold;
    public Style grayItal;
    public Style boldGrayItal;

    private final String myUsername;

    /* Integers representing positions in the doc */
    private int endOfReceived;
    private int endOfPending;

    /** Maps a uniqueID to a message */
    private Map<Integer, IMMessage> pending;

    public MessagesDoc(String _myUsername) {
        myUsername = _myUsername;

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
                oldMessage.getMessage(), false, uniqueID);

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
package client;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.text.*;

public class ConversationPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel otherUsersHeading;
    private final ListModel otherUsersModel;
    private final JList otherUsers;
    private final JScrollPane otherUsersScrollPane;
    private final JLabel inviteHeading;
    private final JTextField inviteField;
    private final JButton inviteButton;
    private final JTextPane messages;
    private final JScrollPane messagesScrollPane;
    private final JTextField newMessage;
    private final JButton sendButton;

    private final MessagesDoc messagesDoc;

    public ConversationPanel() {
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
        
        messagesDoc = new MessagesDoc();
        messages.setDocument(messagesDoc);
        /** TODO remove **/
        try {
            messagesDoc.appendString("Ben: ", messagesDoc.bold);
            messagesDoc.appendString("hi guys", messagesDoc.regular);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        
        

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

    /* TODO remove later */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JTabbedPane tabbedPane = new JTabbedPane();
                JComponent panel1 = new ConversationPanel();
                tabbedPane.addTab("panel1", panel1);
                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

                JFrame main = new JFrame();
                main.add(tabbedPane);
                main.setMinimumSize(new Dimension(600, 400));
                main.setVisible(true);
            }
        });
    }
}

class MessagesDoc extends DefaultStyledDocument {
    public Style regular;
    public Style bold;
    
    public MessagesDoc() {
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(
                StyleContext.DEFAULT_STYLE);
        regular = this.addStyle("regular", defaultStyle);
        bold = this.addStyle("bold", regular);
        StyleConstants.setBold(bold, true);
    }
    
    public void appendString(String s, AttributeSet a) throws BadLocationException {
        /* TODO Implement word wrap in this method */
        this.insertString(this.getLength(), s, a);
    }
}
package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;

public class TopLevelPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final ClientGUI clientGUI;

    private final JLabel otherUsersHeading;
    protected final DefaultListModel otherUsersModel;
    private final JList otherUsers;
    private final JScrollPane otherUsersScrollPane;

    private final JPanel taskButtons;
    private final JButton newRoomButton;
    private final JButton oneOnOneButton;
    private final JButton joinConvButton;
    private final JButton disconnectButton;

    private final JLabel pastConversationsHeading;
    protected final DefaultListModel pastConversationsModel;
    private final JList pastConversations;
    private final JScrollPane pastConversationsScrollPane;

    public TopLevelPanel(ClientGUI _clientGUI) {
        clientGUI = _clientGUI;

        otherUsersHeading = new JLabel("Other users:");
        otherUsersHeading.setName("otherUsersHeading");

        otherUsersModel = new DefaultListModel();

        otherUsers = new JList(otherUsersModel);
        otherUsers.addMouseListener(new DoubleClickUsernameListener(clientGUI));
        otherUsers.setName("otherUsers");
        otherUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        otherUsersScrollPane = new JScrollPane(otherUsers);
        otherUsersScrollPane.setName("otherUsersScrollPane");

        newRoomButton = new JButton("New Room");
        newRoomButton.setName("newRoomButton");
        newRoomButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientGUI.promptForNewRoom();
            }
        });

        oneOnOneButton = new JButton("New One-on-One Chat");
        oneOnOneButton.setName("oneOnOneButton");
        oneOnOneButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientGUI.promptForTwoWayConv();
            }
        });

        joinConvButton = new JButton("Join Conversation");
        joinConvButton.setName("joinConvButton");
        joinConvButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                clientGUI.promptToJoinConv();
            }
        });

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setName("disconnectButton");
        disconnectButton.addActionListener(new DisconnectListener(clientGUI));

        taskButtons = new JPanel();
        taskButtons.setLayout(new BoxLayout(taskButtons, BoxLayout.LINE_AXIS));
        taskButtons.add(newRoomButton);
        taskButtons.add(oneOnOneButton);
        taskButtons.add(joinConvButton);
        taskButtons.add(disconnectButton);

        pastConversationsHeading = new JLabel("Past conversations:");
        pastConversationsHeading.setName("pastConversationsHeading");
        pastConversationsModel = new DefaultListModel();
        pastConversations = new JList(pastConversationsModel);
        pastConversations.setName("pastConversations");
        pastConversations.addMouseListener(new DoubleClickHistoryItemListener(clientGUI));
        pastConversationsScrollPane = new JScrollPane(pastConversations);
        pastConversationsScrollPane.setName("pastConversationsScrollPane");

        /*
         * TODO Comment briefly describing the layout
         */
        JPanel leftColumn = new JPanel();
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.PAGE_AXIS));
        leftColumn.add(otherUsersHeading);
        leftColumn.add(otherUsersScrollPane);
        leftColumn.setMaximumSize(new Dimension(150, Integer.MAX_VALUE));

        GroupLayout layout = new GroupLayout(this);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout
                .createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(leftColumn))
                .addGroup(
                        layout.createParallelGroup().addComponent(taskButtons)
                                .addComponent(pastConversationsHeading)
                                .addComponent(pastConversationsScrollPane)));

        layout.setVerticalGroup(layout
                .createParallelGroup()
                .addGroup(
                        layout.createSequentialGroup().addComponent(leftColumn))
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(taskButtons)
                                .addComponent(pastConversationsHeading)
                                .addComponent(pastConversationsScrollPane)));

        this.setLayout(layout);
    }
}

class DoubleClickHistoryItemListener extends MouseAdapter {
    /*
     * Many thanks to Mohamed Saligh on StackOverflow for explaining how to
     * listen for double clicks
     */
    private final ClientGUI clientGUI;

    public DoubleClickHistoryItemListener(ClientGUI _clientGUI) {
        clientGUI = _clientGUI;
    }

    public void mouseClicked(MouseEvent e) {
        JList list = (JList) e.getSource();
        if (e.getClickCount() == 2) {
            int index = list.locationToIndex(e.getPoint());
            String conv = (String) list.getModel().getElementAt(index);
            // Open a history view
            JDialog dialog = new JDialog(clientGUI, "Conversation history: " + conv);
            JTextPane view = new JTextPane(clientGUI.conversationHistory.historyDocument(conv));
            view.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(view);
            dialog.add(scrollPane);
            dialog.setMinimumSize(new Dimension(300, 200));
            dialog.setVisible(true);
        }
    }
}
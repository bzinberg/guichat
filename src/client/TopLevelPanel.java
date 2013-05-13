package client;

import java.awt.Dimension;

import javax.swing.*;

public class TopLevelPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private final JLabel otherUsersHeading;
    private final ListModel otherUsersModel;
    private final JList otherUsers;
    private final JScrollPane otherUsersScrollPane;

    private final JPanel taskButtons;
    private final JButton newRoomButton;
    private final JButton oneOnOneButton;
    private final JButton disconnectButton;

    private final JLabel pastConversationsHeading;
    private final ListModel pastConversationsModel;
    private final JList pastConversations;
    private final JScrollPane pastConversationsScrollPane;

    public TopLevelPanel() {
        otherUsersHeading = new JLabel("Other users:");
        otherUsersHeading.setName("otherUsersHeading");

        otherUsersModel = new DefaultListModel();

        otherUsers = new JList(otherUsersModel);
        otherUsers.setName("otherUsers");
        otherUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        otherUsersScrollPane = new JScrollPane(otherUsers);
        otherUsersScrollPane.setName("otherUsersScrollPane");

        newRoomButton = new JButton("New Room");
        newRoomButton.setName("newRoomButton");

        oneOnOneButton = new JButton("New One-on-One Chat");
        oneOnOneButton.setName("oneOnOneButton");

        disconnectButton = new JButton("Disconnect");
        disconnectButton.setName("disconnectButton");

        taskButtons = new JPanel();
        taskButtons.setLayout(new BoxLayout(taskButtons, BoxLayout.LINE_AXIS));
        taskButtons.add(newRoomButton);
        taskButtons.add(oneOnOneButton);
        taskButtons.add(disconnectButton);

        pastConversationsHeading = new JLabel("Past conversations:");
        pastConversationsHeading.setName("pastConversationsHeading");
        pastConversationsModel = new DefaultListModel();
        pastConversations = new JList(pastConversationsModel);
        pastConversations.setName("pastConversations");
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

    /* TODO remove later */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JTabbedPane tabbedPane = new JTabbedPane();
                JComponent panel1 = new TopLevelPanel();
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

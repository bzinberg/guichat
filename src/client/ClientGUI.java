package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;

public class ClientGUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private final JTabbedPane tabbedPane;
    private final TopLevelPanel topLevelPanel;
    private final JPanel statusPanel;
    private final JLabel status;

    public ClientGUI() {
        tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        topLevelPanel = new TopLevelPanel();
        tabbedPane.addTab("topLevelPanel", topLevelPanel);
        for (int i = 1; i < 10; i++) {
            String title = "conv" + i;
            ConversationPanel conv = new ConversationPanel();
            tabbedPane.add(title, conv);
            addCloseButton(conv);
        }

        /* Many thanks to krock on StackOverflow for this status bar idea */
        status = new JLabel();
        status.setName("status");
        /* TODO remove */
        status.setText("[status]");
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

        this.getRootPane().setPreferredSize(new Dimension(600, 400));
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

    /* TODO remove later */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ClientGUI main = new ClientGUI();
                main.setVisible(true);
            }
        });
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
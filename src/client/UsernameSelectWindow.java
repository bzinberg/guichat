package client;

import java.awt.Dimension;
import java.awt.event.*;
import java.net.Socket;

import javax.swing.*;

public class UsernameSelectWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Socket socket;
    private final String serverName;

    // "Connected to server [serverName]"
    private final JLabel connectedTo;
    // "[Error text, if any] \n Desired username:"
    private final JLabel usernamePrompt;

    private final JRadioButton customUsername;
    private final JTextField username;
    private final JRadioButton generateUsername;
    private final ButtonGroup typeOfUsername;

    private final JButton okButton;

    public UsernameSelectWindow(Socket _socket, String _serverName) {
        socket = _socket;
        serverName = _serverName;

        connectedTo = new JLabel();
        connectedTo.setName("connectedTo");
        connectedTo.setText("Connected to server " + serverName);

        usernamePrompt = new JLabel();
        usernamePrompt.setName("usernamePrompt");
        usernamePrompt.setText("Desired username:");

        customUsername = new JRadioButton("Custom");
        customUsername.setName("customUsername");
        customUsername.setSelected(true);

        username = new JTextField();
        username.setName("username");
        username.setMinimumSize(new Dimension(100, 20));
        username.setMaximumSize(new Dimension(100, 20));

        generateUsername = new JRadioButton("Generate one for me");
        generateUsername.setName("generateUsername");

        typeOfUsername = new ButtonGroup();
        typeOfUsername.add(customUsername);
        typeOfUsername.add(generateUsername);

        okButton = new JButton("OK");
        okButton.setName("okButton");
        okButton.setMinimumSize(new Dimension(75, 25));
        okButton.setMaximumSize(new Dimension(75, 25));

        /*
         * TODO Comment briefly describing layout
         */
        GroupLayout layout = new GroupLayout(this.getRootPane());
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        layout.setHorizontalGroup(layout
                .createParallelGroup()
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(connectedTo))
                .addGroup(
                        layout.createSequentialGroup().addComponent(
                                usernamePrompt))
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(customUsername)
                                .addComponent(username))
                .addGroup(
                        layout.createSequentialGroup().addComponent(
                                generateUsername))
                .addGroup(layout.createSequentialGroup().addComponent(okButton)));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(
                        layout.createParallelGroup().addComponent(connectedTo))
                .addGroup(
                        layout.createParallelGroup().addComponent(
                                usernamePrompt))
                .addGroup(
                        layout.createParallelGroup()
                                .addComponent(customUsername)
                                .addComponent(username))
                .addGroup(
                        layout.createParallelGroup().addComponent(
                                generateUsername))
                .addGroup(layout.createParallelGroup().addComponent(okButton)));
        
        this.getRootPane().setLayout(layout);
        this.setMinimumSize(new Dimension(300, 180));
    }
    
    /* TODO remove later */
    public static void main(final String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UsernameSelectWindow main = new UsernameSelectWindow(null, "[serverName]");

                main.setVisible(true);
            }
        });
    }
}

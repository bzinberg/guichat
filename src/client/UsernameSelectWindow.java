package client;

import java.awt.Dimension;
import java.awt.event.*;
import java.net.Socket;
import java.io.*;

import javax.swing.*;

public class UsernameSelectWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final String serverName;

    // "Connected to server [serverName]"
    private final JLabel connectedTo;
    // "Desired username:"
    private final JLabel usernamePrompt;

    private final JRadioButton customUsername;
    private final JTextField username;
    private final JRadioButton generateUsername;
    private final ButtonGroup typeOfUsername;

    private final JButton okButton;

    public UsernameSelectWindow(Socket _socket, String _serverName)
            throws IOException {
        socket = _socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
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
        username.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryToRegisterUsername();
            }
        });
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
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryToRegisterUsername();
            }
        });

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
        username.requestFocusInWindow();
    }

    private void tryToRegisterUsername() {
        username.setEnabled(false);
        customUsername.setEnabled(false);
        generateUsername.setEnabled(false);
        okButton.setEnabled(false);

        String desiredName;
        if (customUsername.isSelected()) {
            desiredName = username.getText();
            if (desiredName.isEmpty() || desiredName.contains("\t")
                    || desiredName.contains("\n")) {
                alertAndReenable("Username must be nonempty and cannot contain tabs or newlines.");
                return;
            }
        } else {
            desiredName = "";
        }

        usernamePrompt.setText("Attempting to register username " + desiredName
                + "...");

        if (desiredName.contains("\t")) {
            alertAndReenable("Username may not contain tab characters.");
            return;
        }

        String messageOut = "0" + "\t" + desiredName;
        out.println(messageOut);

        String messageIn = null;
        try {
            messageIn = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            alertAndReenable("I/O error, see stderr for stack trace.");
            return;
        }

        String[] message = messageIn.split("\t", 2);
        if (message.length < 2) {
            alertAndReenable("Received malformed response from server. Oops!");
            return;
        }

        int messageType = Integer.parseInt(message[0]);
        if (messageType == 0) {
            // We were successfully assigned the username
            ClientGUI clientGUI = new ClientGUI(socket, in, out, serverName,
                    message[1].split("\t", 2)[0], message[1]);
            clientGUI.setVisible(true);
            this.dispose();
            return;
        } else if (messageType == 6) {
            alertAndReenable("Could not login with username " + desiredName
                    + ". It was taken.");
            return;
        } else {
            alertAndReenable("Received unexpected message from server. Oops!");
        }
    }

    private void alertAndReenable(String s) {
        JOptionPane.showMessageDialog(this, s);
        usernamePrompt.setText("Desired username:");
        username.setEnabled(true);
        customUsername.setEnabled(true);
        generateUsername.setEnabled(true);
        okButton.setEnabled(true);
    }
}

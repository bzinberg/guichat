package client;

import java.awt.Dimension;
import java.awt.event.*;
import java.net.Socket;
import java.io.*;

import javax.swing.*;

/**
 * Window which prompts the user for a username to use on the server. Complains
 * if the user input is invalid; spawns a ClientGUI upon success.
 */
public class UsernameSelectWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    // Human-readable name of server
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
         * Layout:
         * 
         * ----------------------
         * 
         * Connected to server [serverName]
         * 
         * Desired username:
         * 
         * o Custom |====|
         * 
         * o Generate one for me
         * 
         * | OK |
         * 
         * ----------------------
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
        // Custom window close behavior: go back to connect window
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {
            }

            public void windowClosing(WindowEvent e) {
                try {
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                dispose();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        ConnectWindow connectWindow = new ConnectWindow();
                        connectWindow.setVisible(true);
                    }
                });
            }

            public void windowClosed(WindowEvent e) {
            }

            public void windowIconified(WindowEvent e) {
            }

            public void windowDeiconified(WindowEvent e) {
            }

            public void windowActivated(WindowEvent e) {
            }

            public void windowDeactivated(WindowEvent e) {
            }
        });
        this.setMinimumSize(new Dimension(300, 180));
        username.requestFocusInWindow();
    }

    /**
     * Collects the user input regarding which username to register and attempts
     * to register that username. Complains if the user input is invalid. Waits
     * for server response, lets us know if the name is taken. Upon success
     * registering username, spawns a ClientGUI for our session.
     */
    private void tryToRegisterUsername() {
        // Prevent user from further editing GUI fields for now
        username.setEnabled(false);
        customUsername.setEnabled(false);
        generateUsername.setEnabled(false);
        okButton.setEnabled(false);

        String desiredName;
        if (customUsername.isSelected()) {
            desiredName = username.getText();
            if (desiredName.isEmpty() || desiredName.contains("\t")
                    || desiredName.contains("\n")) {
                dialogAndReenable("Username must be nonempty and cannot contain tabs or newlines.");
                return;
            }
        } else {
            desiredName = "";
        }

        usernamePrompt.setText("Attempting to register username " + desiredName
                + "...");

        String messageOut = network.NetworkConstants.CONNECT + "\t"
                + desiredName;
        out.println(messageOut);

        String messageIn = null;
        try {
            messageIn = in.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            dialogAndReenable("I/O error, see stderr for stack trace.");
            return;
        }

        String[] message = messageIn.split("\t", 2);
        if (message.length < 2) {
            dialogAndReenable("Received malformed response from server. Oops!");
            return;
        }

        String messageType = message[0];
        if (messageType.equals(network.NetworkConstants.INIT_USERS_LIST)) {
            // We were successfully assigned the username
            ClientGUI clientGUI = new ClientGUI(socket, in, out, serverName,
                    message[1].split("\t", 2)[0], message[1]);
            clientGUI.setVisible(true);
            this.dispose();
            return;
        } else if (messageType.equals(network.NetworkConstants.DISCONNECTED)) {
            dialogAndReenable("Could not login with username " + desiredName
                    + ". It was taken.");
            return;
        } else {
            dialogAndReenable("Received unexpected message from server. Oops!");
        }
    }

    /**
     * Displays an alert dialog with text s, then reenables the GUI elements.
     * 
     * @param s
     *            Text of message
     */
    private void dialogAndReenable(String s) {
        JOptionPane.showMessageDialog(this, s);
        usernamePrompt.setText("Desired username:");
        username.setEnabled(true);
        customUsername.setEnabled(true);
        generateUsername.setEnabled(true);
        okButton.setEnabled(true);
    }
}

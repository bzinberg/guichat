package client;

import java.awt.Dimension;
import java.awt.event.*;
import java.io.*;
import java.net.*;

import javax.swing.*;

/**
 * TODO Javadoc
 */
public class ConnectWindow extends JFrame {
    private static final long serialVersionUID = 1L;

    // Server to connect to:
    private final JLabel mainPrompt;
    // "Hostname"
    private final JLabel hostnamePrompt;
    private final JTextField hostname;
    // "Port"
    private final JLabel portPrompt;
    private final JTextField port;

    private final JButton connectButton;

    public ConnectWindow() {
        mainPrompt = new JLabel("Server to connect to:");
        mainPrompt.setName("mainPrompt");
        mainPrompt.setMinimumSize(new Dimension(100, 20));

        hostnamePrompt = new JLabel("Hostname");
        hostnamePrompt.setName("hostnamePrompt");

        hostname = new JTextField();
        hostname.setName("hostname");
        hostname.setMinimumSize(new Dimension(100, 20));
        hostname.setMaximumSize(new Dimension(100, 20));
        hostname.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryToConnect();
            }
        });

        portPrompt = new JLabel("Port");
        portPrompt.setName("portPrompt");

        port = new JTextField();
        port.setName("port");
        port.setMinimumSize(new Dimension(50, 20));
        port.setMaximumSize(new Dimension(50, 20));
        port.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryToConnect();
            }
        });

        connectButton = new JButton("Connect");
        connectButton.setName("connectButton");
        connectButton.setMinimumSize(new Dimension(100, 25));
        connectButton.setMaximumSize(new Dimension(100, 25));
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tryToConnect();
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
                        layout.createSequentialGroup().addComponent(mainPrompt))
                .addGroup(
                        layout.createSequentialGroup()
                                .addComponent(hostnamePrompt)
                                .addComponent(hostname)
                                .addComponent(portPrompt).addComponent(port))
                .addGroup(
                        layout.createSequentialGroup().addComponent(
                                connectButton)));
        layout.setVerticalGroup(layout
                .createSequentialGroup()
                .addGroup(layout.createParallelGroup().addComponent(mainPrompt))
                .addGroup(
                        layout.createParallelGroup()
                                .addComponent(hostnamePrompt)
                                .addComponent(hostname)
                                .addComponent(portPrompt).addComponent(port))
                .addGroup(
                        layout.createParallelGroup()
                                .addComponent(connectButton)));

        this.getRootPane().setLayout(layout);
        this.setMinimumSize(new Dimension(300, 150));
    }

    /**
     * TODO Javadoc
     */
    private void tryToConnect() {
        /* TODO Implement */
        connectButton.setEnabled(false);
        hostname.setEnabled(false);
        port.setEnabled(false);
        mainPrompt.setText("Trying to connect to server...");

        String serverAdress = hostname.getText().trim();
        int portNumber = Integer.parseInt(port.getText());

        try {
            Socket socket = new Socket(serverAdress, portNumber);
            UsernameSelectWindow usernameSelectWindow = new UsernameSelectWindow(
                    socket, hostname.getText());
            usernameSelectWindow.setVisible(true);
            this.dispose();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            dialogAndReenable("Unknown host, see stdout for stack trace.");
        } catch (IOException e) {
            e.printStackTrace();
            dialogAndReenable("I/O error, see stdout for stack trace.");
        }
    }

    private void dialogAndReenable(String s) {
        JOptionPane.showMessageDialog(this, s);
        hostname.setEnabled(true);
        port.setEnabled(true);
        connectButton.setEnabled(true);
    }

}

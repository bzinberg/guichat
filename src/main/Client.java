package main;

import javax.swing.SwingUtilities;

/**
 * GUI chat client runner.
 */
public class Client {

    /**
     * Start a GUI chat client.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                client.ConnectWindow window = new client.ConnectWindow();
                window.setVisible(true);
            }
        });
    }
}

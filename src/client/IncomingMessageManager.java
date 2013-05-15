package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles incoming communication from the server. Enqueues messages and handles
 * them (from the Swing event dispatch thread) when we are able.
 */
public class IncomingMessageManager {
    private final BlockingQueue<String> incomingMessages;
    private final BufferedReader in;
    private volatile boolean running = true;

    // The GUI for this session
    private final ClientGUI clientGUI;

    public IncomingMessageManager(BufferedReader _in, ClientGUI _clientGUI) {
        in = _in;
        incomingMessages = new LinkedBlockingQueue<String>();
        clientGUI = _clientGUI;
    }

    /**
     * Start up a Publisher and a Subscriber so that we can start collecting and
     * handling incoming messages from the server.
     */
    public void start() {
        Publisher publisher = new Publisher();
        Subscriber subscriber = new Subscriber();
        subscriber.start();
        publisher.start();
    }

    /**
     * Set up an IncomingMessageWorker to handle the given message from the
     * event dispatch thread.
     * 
     * @param message
     *            The text of the message from server
     * @throws BadServerMessageException
     *             If the message is malformed
     */
    private void handleIncomingMessage(String message)
            throws BadServerMessageException {
        IncomingMessageWorker worker = new IncomingMessageWorker(message,
                clientGUI);
        worker.execute();
    }

    /**
     * Stops the publisher and subscriber so that we stop collecting handling
     * incoming messages
     */
    public void stop() {
        running = false;
    }

    /**
     * Publisher which continually reads messages from the server and adds them
     * to the queue of messages to handle.
     */
    private class Publisher extends Thread {
        public void run() {
            while (running) {
                try {
                    String next = in.readLine();
                    if (next == null) { // Server closed!
                        clientGUI.disconnect();
                        clientGUI.dispose();

                        ConnectWindow connectWindow = new ConnectWindow();
                        connectWindow.setVisible(true);
                    } else
                        incomingMessages.add(next);
                } catch (IOException e) {
                    if (!e.getMessage().equals("Socket closed")) // Disconnected.
                        e.printStackTrace();
                }
            }
        }
    }

    /**
     * Subscriber which continually takes messages from the queue and sets up
     * <tt>IncomingMessageWorker</tt>s to handle them on the Swing event
     * dispatch thread.
     */
    private class Subscriber extends Thread {
        public void run() {
            while (running) {
                String next;
                try {
                    next = incomingMessages.take();
                    handleIncomingMessage(next);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BadServerMessageException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

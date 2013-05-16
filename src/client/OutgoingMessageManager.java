package client;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Handles outgoing communication to the server. Enqueues messages and sends
 * them off as it is able.
 */
public class OutgoingMessageManager {
    private final BlockingQueue<MessageToServer> outgoingMessages;
    private final PrintWriter out;
    private volatile boolean running = true;

    public OutgoingMessageManager(PrintWriter _out) {
        outgoingMessages = new LinkedBlockingQueue<MessageToServer>();
        out = _out;
    }

    /**
     * Sends the given message to the server, if it has not yet been cancelled.
     */
    private void send(MessageToServer message) {
        if (!message.isCanceled()) {
            out.println(message.getMessageText());
        }
    }

    /**
     * Enqueues the given message to be sent to the server.
     */
    public void add(MessageToServer message) {
        outgoingMessages.add(message);
    }

    /**
     * Starts up a Subscriber so that we can start sending our enqueued messages
     * to the server.
     */
    public void start() {
        Subscriber subscriber = new Subscriber();
        subscriber.start();
    }

    /**
     * Stops the Subscriber so that we stop sending outgoing messages to the
     * server.
     */
    public void stop() {
        running = false;
    }

    /**
     * Subscriber which continually takes messages from the queue and sends them
     * out to the server (if they have not been cancelled).
     */
    private class Subscriber extends Thread {
        public void run() {
            while (running) {
                try {
                    MessageToServer next = outgoingMessages.take();
                    send(next);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

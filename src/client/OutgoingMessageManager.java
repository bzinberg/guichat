package client;

import java.io.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class OutgoingMessageManager {
    private final BlockingQueue<MessageToServer> outgoingMessages;
    private final PrintWriter out;
    private volatile boolean running = true;

    public OutgoingMessageManager(PrintWriter _out) {
        outgoingMessages = new LinkedBlockingQueue<MessageToServer>();
        out = _out;
    }

    private void send(MessageToServer message) {
        if (!message.isCancelled()) {
            out.println(message.getMessageText());
        }
    }

    public void add(MessageToServer message) {
        outgoingMessages.add(message);
    }

    public void start() {
        Subscriber subscriber = new Subscriber();
        subscriber.start();
    }
    
    public void stop() {
        running = false;
    }
    

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

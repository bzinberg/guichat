package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class IncomingMessageManager {
    private final BlockingQueue<String> incomingMessages;
    private final BufferedReader in;
    private volatile boolean running = true;

    ClientGUI clientGUI;

    public IncomingMessageManager(BufferedReader _in, ClientGUI _clientGUI) {
        in = _in;
        incomingMessages = new LinkedBlockingQueue<String>();
        clientGUI = _clientGUI;
    }

    public void start() {
        Publisher publisher = new Publisher();
        Subscriber subscriber = new Subscriber();
        subscriber.start();
        publisher.start();
    }

    private void handleIncomingMessage(String message) throws BadServerMessageException {
        IncomingMessageWorker worker = new IncomingMessageWorker(message,
                clientGUI);
        worker.execute();
    }
    
    public void stop() {
        running = false;
    }

    private class Publisher extends Thread {
        public void run() {
            while (running) {
                try {
                    String next = in.readLine();
                    if(next == null) { // Server closed!
                    	clientGUI.disconnect();
                    	clientGUI.dispose();
                    	
                        ConnectWindow connectWindow = new ConnectWindow();
                        connectWindow.setVisible(true);
                    }
                    else
                    	incomingMessages.add(next);
                } catch(IOException e) {
                	if(!e.getMessage().equals("Socket closed")) //Disconnected.
                		e.printStackTrace();
                }
            }
        }
    }

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

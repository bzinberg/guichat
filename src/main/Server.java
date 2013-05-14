package main;

import java.io.IOException;

import network.NetworkConstants;

import server.IMServer;

/**
 * Chat server runner.
 */
public class Server {

    /**
     * Start a chat server.
     */
	public static void main(String[] args) {
		try {
			IMServer server = new IMServer(NetworkConstants.DEFAULT_PORT);
			server.run();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
}

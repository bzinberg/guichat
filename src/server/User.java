package server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;

import main.NetworkConstants;

public class User extends Thread {
	
	private String name;
	private final Socket socket;
	private final HashSet<Conversation> convSet;
	private final PrintWriter out;
	private final BufferedReader in;
	private final IMServer server;
	
	/**
	 * Constructs an user object with given attributes
	 * @param name
	 * @param socket
	 */
	User(IMServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.convSet = new HashSet<Conversation>();
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	private void send(String s) {
		out.println(s);
		out.flush();
	}
	
	private void close() {
		in.close();
		out.close();
	}
	
	@Override
	public void run() {
		
	}
	
	boolean addConversation(Conversation conv) {
		boolean b = convSet.add(conv);
		if (!b) return false;
		String message = NetworkConstants.ENTERED_CONV + "\t" + conv.toString();
		send(message);
		return true;
	}
	
	void addUserInConversation(User u, String convName) {
		String message = NetworkConstants.ADDED_TO_CONV + "\t" + convName;
		send(message);
	}
	
	void removeUserInConversation(User u, String convName) {
		String message = NetworkConstants.REMOVED_FROM_CONV + "\t" + convName;
		send(message);
	}
	
	boolean removeConversation(Conversation conv) {
		boolean b = convSet.remove(conv);
		return b;
	}
	
	String getUsername() {
		return name;
	}
	
	@Override
	boolean equals(Object o) {
		if ()
	}
	
	boolean isInConversation(Conversation conv) {
		return convSet.contains(conv);
	}

}
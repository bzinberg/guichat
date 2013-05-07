package server;

import java.io.BufferedReader;
import java.io.IOException;
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
	 * @throws IOException 
	 */
	User(IMServer server, Socket socket) throws IOException {
		this.server = server;
		this.socket = socket;
		this.convSet = new HashSet<Conversation>();
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	/**
	 * send the string to the given user
	 * @param s
	 */
	private void send(String s) {
		out.println(s);
		out.flush();
	}
	
	@Override
	public void run() {
		
	}
	
	/**
	 * add the given conversation to the user's list of conversations
	 * and notify him of the current state of this conversation
	 * @param conv
	 * @return true iff the user is not already in this conversation
	 */
	boolean addConversation(Conversation conv) {
		boolean b = convSet.add(conv);
		if (!b) return false;
		String message = NetworkConstants.ENTERED_CONV + "\t" + conv.toString();
		send(message);
		return true;
	}
	
	/**
	 * notify the user that a different user is added to one of the conversations he is in
	 * @param u
	 * @param convName
	 */
	void addUserInConversation(User u, String convName) {
		String message = NetworkConstants.ADDED_TO_CONV + "\t" + convName;
		send(message);
	}
	
	/**
	 * notify the user that a different user is removed from one of the conversations he is in
	 * @param u
	 * @param convName
	 */
	void removeUserInConversation(User u, String convName) {
		String message = NetworkConstants.REMOVED_FROM_CONV + "\t" + convName;
		send(message);
	}
	
	/**
	 * remove a conversation from the list of conversations this user is in
	 * @param conv
	 * @return
	 */
	boolean removeConversation(Conversation conv) {
		boolean b = convSet.remove(conv);
		return b;
	}
	
	/**
	 * returns this user's name
	 * @return
	 */
	String getUsername() {
		return name;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof User) return false;
		return ((User) o).getUsername().equals(name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	boolean isInConversation(Conversation conv) {
		return convSet.contains(conv);
	}
	
	/**
	 * notify the user that a new message has been added to this conversation
	 * @param u
	 * @param m
	 * @param uniqueID
	 * @param convName
	 */
	void sendMessage(User u, String m, int uniqueID, String convName) {
		String message = NetworkConstants.IM + "\t" + name + "\t" + convName + "\t" + uniqueID + "\t" + m;
		send(message);
	}

}
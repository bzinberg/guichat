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
		synchronized(out) {
			out.println(s);
			out.flush();
		}
	}
	
	@Override
	public void run() {
		try {
			for (String line = in.readLine(); line != null && name == null; line = in.readLine())
				handleConnection(line);
			for (String line = in.readLine(); line != null; line = in.readLine())
            	handleRequest(line);
		}
		catch(Exception e) {
			try { in.close(); }
			catch(IOException ee) {}
			try { socket.close(); }
			catch(IOException ee) {}
			out.close();
			server.disconnectUser(name);
		}
	}
	
	private boolean handleConnection(String req) {
		if(req == null)
			return false;
		String[] args = req.split("\t");
		if(args.length != 2)
			return false;
		else if(!args[1].matches(NetworkConstants.USERNAME))
			return false;
		
		name = args[1];
		boolean connected = server.connectUser(this);
		if(!connected)
			name = null;
		return connected;
	}
	
	/**
	 * Handles the given request and returns true.  If the request
	 * does not follow the specified grammar or cannot be processed,
	 * returns false.
	 * 
	 * @param req The client's request.
	 * @return True if the request is valid and is properly executed.
	 */
	private boolean handleRequest(String req) throws InterruptedException {
		if(req == null)
			return false;
		String[] args = req.split("\t");
		if(args.length == 0)
			return false;
		else if(args[0].equals(NetworkConstants.IM))
			return im(args);
		else if(args[0].equals(NetworkConstants.NEW_CONV))
			return newConv(args);
		else if(args[0].equals(NetworkConstants.ADD_TO_CONV))
			return addToConv(args);
		else if(args[0].equals(NetworkConstants.ENTER_CONV))
			return enterConv(args);
		else if(args[0].equals(NetworkConstants.EXIT_CONV))
			return exitConv(args);
		else if(args[0].equals(NetworkConstants.DISCONNECT)) {
			throw new InterruptedException();
		}
		else
			return false;
	}
	
	private boolean im(String[] args) {
		if(args == null || args.length != 4)
			return false;
		if(args[1] == null || args[2] == null || args[3] == null)
			return false;
		if(!args[1].matches(NetworkConstants.CONV_NAME)
				|| !args[2].matches(NetworkConstants.IM_ID)
				|| !args[3].matches(NetworkConstants.MESSAGE))
			return false;
		return server.sendMessage(name, args[1], Integer.parseInt(args[2]), args[3]);
	}

	private boolean newConv(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.NEW_CONV_NAME))
			return false;
		return server.newConversation(name, args[1]);
	}

	private boolean addToConv(String[] args) {
		if(args == null || args.length != 3)
			return false;
		if(args[1] == null || args[2] == null)
			return false;
		if(!args[1].matches(NetworkConstants.USERNAME)
				|| !args[2].matches(NetworkConstants.CONV_NAME))
			return false;
		return server.addToConversation(args[1], args[2]);
	}

	private boolean enterConv(String[] args) {
		
	}

	private boolean exitConv(String[] args) {
		return false;
	}

	synchronized void removeFromConversations() {
		for(Conversation c : convSet) {
			c.remove(this);
		}
	}
	
	/**
	 * add the given conversation to the user's list of conversations
	 * and notify him of the current state of this conversation
	 * @param conv
	 * @return true iff the user is not already in this conversation
	 */
	synchronized boolean addConversation(Conversation conv) {
		boolean b = convSet.add(conv);
		if (!b) return false;
		sendEnteredConvMessage(conv);
		return true;
	}

	/**
	 * remove a conversation from the list of conversations this user is in
	 * @param conv
	 * @return
	 */
	synchronized boolean removeConversation(Conversation conv) {
		return convSet.remove(conv);
	}
	
	void sendEnteredConvMessage(Conversation conv) {
		String message = NetworkConstants.ENTERED_CONV + "\t" + conv.toString();
		send(message);
	}
	
	/**
	 * notify the user that a different user is added to one of the conversations he is in
	 * @param u
	 * @param convName
	 */
	void sendAddedToConvMessage(User u, String convName) {
		String message = NetworkConstants.ADDED_TO_CONV + "\t" + u.getUsername()
				+ "\t" + convName;
		send(message);
	}
	
	/**
	 * notify the user that a different user is removed from one of the conversations he is in
	 * @param u
	 * @param convName
	 */
	void sendRemovedFromConvMessage(User u, String convName) {
		String message = NetworkConstants.REMOVED_FROM_CONV + "\t" + convName;
		send(message);
	}
	
	void sendConnectedMessage(User u) {
		String message = NetworkConstants.CONNECTED + "\t" + u.getUsername();
		send(message);
	}

	void sendDisconnectedMessage(User u) {
		String message = NetworkConstants.DISCONNECTED + "\t" + u.getUsername();
		send(message);
	}

	/**
	 * notify the user that a new message has been added to this conversation
	 * @param u
	 * @param m
	 * @param uniqueID
	 * @param convName
	 */
	void sendIMMessage(User u, String m, int messageId, String convName) {
		String message = NetworkConstants.IM + "\t"
				+ u.getUsername() + "\t"
				+ convName + "\t"
				+ messageId + "\t" +
				m;
		send(message);
	}
	
	void sendNewConvReceiptMessage(boolean success, String convName) {
		String message = NetworkConstants.NEW_CONV_RECEIPT + "\t"
				+ (success ? NetworkConstants.SUCCESS : NetworkConstants.FAILURE) + "\t"
				+ convName;
		send(message);
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
		if (!(o instanceof User)) return false;
		return ((User) o).getUsername().equals(name);
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
	synchronized boolean isInConversation(Conversation conv) {
		return convSet.contains(conv);
	}

}
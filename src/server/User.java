package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import main.NetworkConstants;

public class User extends Thread {
	
	private String name;
	private final Socket socket;
	private final Set<Conversation> conversations;
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
		this.conversations = new HashSet<Conversation>();
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	/**
	 * Sends a message to the client by printing s to this.socket,
	 * followed by a newline.
	 * 
	 * @param s The message to send to the client.
	 */
	private void send(String s) {
		synchronized(out) {
			out.println(s);
			out.flush();
		}
	}
	
	/**
	 * Reads from this.socket. Tries to connect the client upon encountering
	 * a connect message by calling handleConnection. If the client has sent
	 * a valid username, tries to process the client's request by calling handleRequest.
	 * 
	 * Upon Exception or disconnection, closes this.in, this.socket, and this.out,
	 * removes this from this.server, and removes this from all conversations.
	 */
	@Override
	public void run() {
		try {
			String line;
			for (line = in.readLine(); line != null && name == null; line = in.readLine())
				handleConnection(line);
			for (; line != null; line = in.readLine())
            	handleRequest(line);
		}
		catch(Exception e) {
		}
		finally {
			try { in.close(); }
			catch(IOException ee) {}
			try { socket.close(); }
			catch(IOException ee) {}
			out.close();
			removeFromAllConversations();
			server.disconnectUser(name);
		}
	}
	
	/**
	 * Handles a connection by trying to connect the user.  Returns true if
	 * the user is successfully connected.  Returns false if the request
	 * does not follow the specified grammar for a connect message or if the
	 * specified username is already taken.
	 * 
	 * Sets this.name to the specified username if the user is successfully
	 * connected.
	 * 
	 * @param req The client's request.
	 * @return
	 */
	private boolean handleConnection(String req) {
		if(req == null)
			return false;
		String[] args = req.split("\t", -1);
		if(args.length != 2)
			return false;
		else if(!args[1].matches(NetworkConstants.NEW_USERNAME))
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
		String[] args = req.split("\t", -1);
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
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.CONV_NAME))
			return false;
		return server.addToConversation(name, args[1]);
	}

	private boolean exitConv(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.CONV_NAME))
			return false;
		return server.removeFromConversation(name, args[1]);
	}

	private synchronized void removeFromAllConversations() {
		Object[] convCopy = conversations.toArray();
		for(Object c : convCopy)
			server.removeFromConversation(name, ((Conversation)c).getName());
	}
	
	/**
	 * add the given conversation to the user's list of conversations
	 * and notify him of the current state of this conversation
	 * @param conv
	 * @return true iff the user is not already in this conversation
	 */
	synchronized boolean addConversation(Conversation conv) {
		boolean b = conversations.add(conv);
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
		return conversations.remove(conv);
	}
	
	void sendInitUsersListMessage(Object[] users) {
		StringBuilder message = new StringBuilder(NetworkConstants.INIT_USERS_LIST);
		message.append("\t");
		message.append(name);
		for (Object u : users) {
			message.append("\t");
			message.append(((User) u).getUsername());
		}
		send(message.toString());
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
		String message = NetworkConstants.REMOVED_FROM_CONV + "\t"
				+ u.getUsername() + "\t"
				+ convName;
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
	
	/**
	 * Sets this.name to the given string.
	 * 
	 * Should not be called when this has been added to server.users.
	 * 
	 * @param username The string to which to set this.name. Must contain no
	 * 		  newlines or tabs and must be non-null.  Also,
	 * 		  1 <=username.length() <= 256.
	 */
	void setUsername(String username) {
		this.name = username;
	}
	
	synchronized boolean isInConversation(Conversation conv) {
		return conversations.contains(conv);
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
	
}
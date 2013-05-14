package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import network.NetworkConstants;


public class User extends Thread {
	
	private String name;
	private final Socket socket;
	private final Set<Conversation> conversations;
	private final PrintWriter out;
	private final BufferedReader in;
	private final IMServer server;
	
	/**
	 * Constructs an instance of User for the given server and for the client at
	 * the given socket.
	 * 
	 * @param server The server to which to add this once login has been validated.
	 * @param socket The socket on which there is a connection with the client.
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
	 * specified username is already taken.  If the request does not follow
	 * the specified grammar, sends an error message to the client.
	 * 
	 * Sets this.name to the specified username if the user is successfully
	 * connected.
	 * 
	 * @param req The client's request.
	 * @return True if the call to handleConnection resulted in adding this to
	 * 		   this.server; false otherwise.
	 */
	private boolean handleConnection(String req) {
		if(req == null) {
			sendErrorMessage(req);
			return false;
		}
		String[] args = req.split("\t", -1);
		if(args.length != 2 || !args[1].matches(NetworkConstants.NEW_USERNAME)) {
			sendErrorMessage(req);
			return false;
		}
		
		name = args[1];
		boolean connected = server.connectUser(this);
		if(!connected)
			name = null;
		return connected;
	}
	
	/**
	 * Handles the given request and returns true.  If the request
	 * does not follow the specified grammar or cannot be processed,
	 * returns false and sends an error message to the client.
	 * 
	 * @param req The client's request.
	 * @return True if the request is valid and is properly executed.
	 */
	private boolean handleRequest(String req) throws InterruptedException {
		boolean processed = false;
		if(req == null)
			processed = false;
		else {
			String[] args = req.split("\t", -1);
			if(args.length == 0)
				processed = false;
			else if(args[0].equals(NetworkConstants.IM))
				processed = im(args);
			else if(args[0].equals(NetworkConstants.NEW_CONV))
				processed = newConv(args);
			else if(args[0].equals(NetworkConstants.ADD_TO_CONV))
				processed = addToConv(args);
			else if(args[0].equals(NetworkConstants.ENTER_CONV))
				processed = enterConv(args);
			else if(args[0].equals(NetworkConstants.EXIT_CONV))
				processed = exitConv(args);
			else if(args[0].equals(NetworkConstants.DISCONNECT))
				throw new InterruptedException();
			else if(args[0].equals(NetworkConstants.RETRIEVE_PARTICIPANTS))
				processed = retrieveParticipants(args);
			else if(args[0].equals(NetworkConstants.TWO_WAY_CONV))
				processed = twoWayConv(args);
		}
		if(!processed)
			sendErrorMessage(req);
		return processed;
	}
	
	/**
	 * Processes an im request with the given arguments.
	 * @param args The request arguments.
	 * @return True if the message is successfully sent; false otherwise.
	 */
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

	/**
	 * Processes a new conversation request with the given arguments.
	 * @param args The request arguments.
	 * @return True if an attempt is made by the server to set up the new conversation;
	 * 		   false otherwise.
	 */
	private boolean newConv(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.NEW_CONV_NAME))
			return false;
		server.newConversation(name, args[1]);
		return true;
	}

	/**
	 * Processes an add to conversation request with the given arguments.
	 * @param args The request arguments.
	 * @return True if the specified client is successfully added to the specified
	 * 		   conversation; false otherwise.
	 */
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

	/**
	 * Processes an enter conversation request with the given arguments.
	 * @param args The request arguments.
	 * @return True if the client successfully enters the specified conversation;
	 * 		   false otherwise.
	 */
	private boolean enterConv(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.CONV_NAME))
			return false;
		return server.addToConversation(name, args[1]);
	}

	/**
	 * Processes an exit conversation request with the given arguments.
	 * @param args The request arguments.
	 * @return True if the client successfully exits the specified conversation;
	 * 		   false otherwise.
	 */
	private boolean exitConv(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.CONV_NAME))
			return false;
		return server.removeFromConversation(name, args[1]);
	}

	/**
	 * Processes a retrieve participants request with the given arguments.
	 * @param args The request arguments.
	 * @return True if the participants of the specified conversation are successfully
	 * 		   sent to the client; false otherwise.
	 */
	private boolean retrieveParticipants(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.CONV_NAME))
			return false;
		return server.retrieveParticipants(name, args[1]);
	}

	/**
	 * Processes a two-way conversation request with the given arguments.
	 * @param args The request arguments.
	 * @return True if the two-way conversation is successfully set up;
	 * 		   false otherwise.
	 */
	private boolean twoWayConv(String[] args) {
		if(args == null || args.length != 2)
			return false;
		if(args[1] == null || !args[1].matches(NetworkConstants.USERNAME))
			return false;
		return server.twoWayConversation(name, args[1]);
	}

	/**
	 * Removes this from every Conversation it is a participant of (i.e., all
	 * Conversations in this.conversations).
	 */
	private synchronized void removeFromAllConversations() {
		Object[] convCopy = conversations.toArray();
		for(Object c : convCopy)
			server.removeFromConversation(name, ((Conversation)c).getName());
	}
	
	/**
	 * Adds conv to this.conversations and sends an entered conversation message to the
	 * client corresponding to this.  (addConversation is called by conv with itself as
	 * a parameter.  conv deals with sending added to conversation messages to its other
	 * Users.)  Returns whether or not conv was added, i.e., whether or not this.conversations
	 * has changed as a result of the call to addConversation.
	 * 
	 * Requires that conv be non-null.
	 * 
	 * @param conv The Conversation to add to this.conversations.
	 * @return True if the Conversation was added to this.conversations; false otherwise.
	 */
	synchronized boolean addConversation(Conversation conv) {
		boolean b = conversations.add(conv);
		if (!b) return false;
		sendEnteredConvMessage(conv);
		return true;
	}

	/**
	 * Removes conv from this.conversations.  (removeConversation is called by conv with
	 * itself as a parameter.  conv deals with sending removed from conversation messages
	 * to its Users.)  Returns whether or not conv was added, i.e., whether or not
	 * this.conversations has changed as a result of the call to addConversation.
	 * 
	 * Requires that conv be non-null.
	 * 
	 * @param conv The Conversation to remove from this.conversations.
	 * @return True if the Conversation was removed from this.conversations; false otherwise.
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
			message.append(((User)u).getUsername());
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
	
	void sendParticipantsMessage(Object[] users) {
		StringBuilder message = new StringBuilder(NetworkConstants.PARTICIPANTS);
		for (Object u : users) {
			message.append("\t");
			message.append(((User)u).getUsername());
		}
		send(message.toString());
	}
	
	void sendErrorMessage(String m) {
		String message = NetworkConstants.ERROR + "\t" + m;
		send(message);
	}
	
	/**
	 * Accessor method for this.name.
	 * @return this.name, the username of this client.
	 */
	String getUsername() {
		return name;
	}
	
	/**
	 * Sets this.name to the given string.
	 * 
	 * Should not be called when this has been added to server.users or on
	 * any thread other than this.
	 * 
	 * @param username The string to which to set this.name. Must contain no
	 * 		  newlines or tabs and must be non-null.  Also,
	 * 		  1 <=username.length() <= 256.
	 */
	void setUsername(String username) {
		this.name = username;
	}

	/**
	 * Returns whether or not this is equal to o.
	 * 
	 * this is equal to o if o is an instance of User and has a name 
	 * equal to this.name.
	 * 
	 * @return True if this is equal to o; false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User)) return false;
		String oName = ((User) o).getUsername();
		return oName == name || (name != null && name.equals(oName));
	}
	
	/**
	 * Returns an integer representation of this, given by this.name.hashCode()
	 * if this.name is non-null and by 0 if this.name is null.
	 * 
	 * @return The hashCode of this.name if this.name is non-null; 0 otherwise.
	 */
	@Override
	public int hashCode() {
		if(name == null) return 0;
		return name.hashCode();
	}
	
}
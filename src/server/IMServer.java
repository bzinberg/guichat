package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IMServer implements Runnable {

	private final Map<String, User> users;
	private final Map<String, Conversation> conversations;
	private final ServerSocket serverSocket;
	
	/**
	 * Creates a new instance of IMServer on the specified port.
	 * Initializes users and conversations to be empty.
	 * 
	 * @param port The port on which to listen for user connections.
	 * @throws IOException If server socket cannot be set up on this port.
	 */
	public IMServer(int port) throws IOException {
		users = new HashMap<String, User>();
		conversations = new HashMap<String, Conversation>();
		serverSocket = new ServerSocket(port);
	}
	
	/**
	 * Sends the message m to all clients in the conversation specified by
	 * convName with sender name username and ID messageId.  The variable messageId
	 * should be unique among all messages sent by this client to the
	 * conversation associated with convName.
	 * 
	 * Fails to send message if username is null, not in users, or not in the
	 * specified conversation or if there is no conversations associated with
	 * convName.
	 * 
	 * @param username The name of the User sending the message.
	 * @param convName The string associated with the conversation to which
	 * 		  to send the message.
	 * @param messageId The message ID, unique among messages sent by the User.
	 * @param m The message string.
	 * @return True if the message is properly sent, false otherwise.
	 */
	boolean sendMessage(String username, String convName, int messageId, String m) {
		Conversation conv;
		User u;
		if(username == null)
			return false;
		synchronized(users) {
			if(!users.containsKey(username))
				return false;
			u = users.get(username);
		}
		synchronized(conversations) {
			if(!conversations.containsKey(convName))
				return false;
			conv = conversations.get(convName);
		}
		synchronized(conv) {
			if(!conv.contains(u))
				return false;
			conv.sendMessage(u, m, messageId);
		}
		return true;
	}
	
	/**
	 * If convName is non-null and non-empty and there is no Conversation
	 * associated with convName in this.conversations, creates a new
	 * Conversation with name convName containing only a User with the given
	 * username and adds it to this.conversations.  If convName is null or
	 * empty, creates a new Conversation associated with a unique String
	 * containing only a User with the given username and adds it to
	 * this.conversations.  Sends a new conversation receipt
	 * message to the client represented by u with information on whether
	 * or not the conversation was successfully created.  newConversation
	 * is thread-safe because it synchronizes on conversations to prevent
	 * multiple users from creating new conversations, which could possibly
	 * have the same name, at the same time.
	 * 
	 * Fails to create a new conversation if username is null or not in users or
	 * if convName already refers to a Conversation in conversations.
	 * 
	 * @param username The name of the User creating the conversation
	 * @param convName The name of the conversation to be created, or null
	 * 		  or "" for an auto-generated name.
	 * @return True if the conversation is properly created, false otherwise.
	 */
	boolean newConversation(String username, String convName) {
		User u;
		boolean success = false;
		if(username == null)
			return false;
		synchronized(users) {
			if(!users.containsKey(username))
				return false;
			u = users.get(username);
		}
		if(convName == null || convName.equals("")) {
			String genConvName;
			while(!success) {
				genConvName = String.valueOf(new Random().nextLong());
				synchronized(conversations) {
					success = !conversations.containsKey(genConvName);
					if(success)
						conversations.put(genConvName, new Conversation(genConvName, u));
				}
				if(success)
					u.sendNewConvReceiptMessage(success, genConvName);
			}
		}
		else {
			synchronized(conversations) {
				success = !conversations.containsKey(convName);
				if(success)
					conversations.put(convName, new Conversation(convName, u));
			}
			u.sendNewConvReceiptMessage(success, convName);
		}
		return success;
	}
	
	/**
	 * If there is a Conversation associated with convName, adds the User
	 * corresponding to username to that Conversation and sends an added
	 * to conversation message to to every other client in this conversation,
	 * and sends to the given User a entered conversation message.  If there
	 * is no Conversation associated with convName, sends a removed from
	 * conversation message to the client with the given username.
	 * 
	 * Fails to add the User to the conversation if username is null, not in users,
	 * already in the conversation, or if there is no Conversation associated
	 * with convName.  In the last case, sends a removed from conversation
	 * message to the client with the given username.
	 * 
	 * @param username The name of the user to add.
	 * @param convName The conversation to which to add the User.
	 * @return True if the User was properly added to the conversation.
	 */
	boolean addToConversation(String username, String convName) {
		Conversation conv;
		User u;
		boolean success = false;
		if(username == null)
			return false;
		synchronized(users) {
			if(!users.containsKey(username))
				return false;
			u = users.get(username);
		}
		synchronized(conversations) {
			conv = conversations.get(convName);
		}
		if(conv == null) { // No conv associated with convName.
			u.sendRemovedFromConvMessage(u, convName);
			return false;
		}
		synchronized(conv) {
			success = !conv.contains(u);
			if(success)
				conv.add(u);
		}
		return success;
	}
	
	/**
	 * If there is a Conversation associated with convName, removes the User
	 * with the given username from the Conversation, sends a removed from
	 * conversation message to every other User in the Conversation, and removes
	 * the Conversation from this.conversations if the Conversation contains
	 * no Users after removing the User with the given username.
	 * 
	 * Fails to remove the User from the conversation if usename is null, not in
	 * users, not in the conversation, or if there is no Conversation
	 * associated with convName in conversations.
	 * 
	 * @param username The name of the User to remove.
	 * @param convName The string corresponding to the conversation
	 * 		  from which to remove the User.
	 * @return True if the User is properly removed from the conversation,
	 * 		   false otherwise.
	 */
	boolean removeFromConversation(String username, String convName) {
		Conversation conv;
		User u;
		if(username == null)
			return false;
		synchronized(users) {
			if(!users.containsKey(username))
				return false;
			u = users.get(username);
		}
		synchronized(conversations) {
			conv = conversations.get(convName);
		}
		if(conv == null)
			return false;
		synchronized(conv) {
			if(!conv.contains(u))
				return false;
			conv.remove(u);
			if(conv.isEmpty()) {
				synchronized(conversations) {
					conversations.remove(convName);
				}
			}
		}
		return true;
	}
	
	/**
	 * Removes the User with the given username from each of his conversations,
	 * sending a removed from conversation message to every other User in
	 * the conversation, using remove, and removing empty conversations from
	 * this.conversations.  Removes the given User from this.users.  Sends a
	 * disconnected message to all clients if username was in users.
	 * 
	 * If username is null, does nothing.
	 * 
	 * @param username The name of the User to disconnect.
	 */
	void disconnectUser(String username) {
		User u;
		Object[] usersArray;
		if(username == null)
			return;
		synchronized(users) {
			if(!users.containsKey(username))
				return;
			u = users.remove(username);
			usersArray = users.values().toArray();
		}
		for(Object v : usersArray)
			((User)v).sendDisconnectedMessage(u);
	}

	/**
	 * Adds u to users if u is non-null.  Sends an initial users list message
	 * to u and a connected message to all other Users in this.users if u is not
	 * null and not already in users.  Sends a disconnected message to u if u is
	 * already in users.
	 * 
	 * @param u The User to add.
	 * @return True if u was properly added, false if u is null or
	 * 		   if a User with the same name is already in users.
	 */
	boolean connectUser(User u) {
		boolean added = false;
		Object[] usersArray = new Object[0];
		if(u == null)
			return false;
		synchronized(users) {
			if(!users.containsKey(u.getUsername())) {
				users.put(u.getUsername(), u);
				usersArray = users.values().toArray();
				added = true;
			}
		}
		if(added) {
			for(Object v : usersArray) {
				if(!u.equals(v))
					((User)v).sendConnectedMessage(u);
			}
			u.sendInitUsersListMessage(usersArray);
		}
		else
			u.sendDisconnectedMessage(u);
		return added;
	}
	
	public void run () {
		while(true) {
			Socket socket;
			try {
				socket = serverSocket.accept(); 
				new User(this, socket).start();
			}
			catch(IOException e) { }
		}
	}
}

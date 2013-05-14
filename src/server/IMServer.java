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
		User u = userByUsername(username);
		if(u == null)
			return false;
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
	 * this.conversations.  Sends an entered conversation
	 * message to the client.
	 * 
	 * Fails to create a new conversation if username is null or not in users or
	 * if convName already refers to a Conversation in conversations.
	 * 
	 * @param username The name of the User creating the conversation
	 * @param convName The name of the conversation to be created, or null
	 * 		  or "" for an auto-generated name.
	 * @return True if the conversation is successfully created; false otherwise.
	 */
	boolean newConversation(String username, String convName) {
		boolean success = false;
		User u = userByUsername(username);
		if(u == null)
			return false;
		if(convName == null || convName.equals("")) {
			String genConvName = null;
			while(!success) {
				genConvName = "conversation" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
				synchronized(conversations) {
					success = !conversations.containsKey(genConvName);
					if(success)
						conversations.put(genConvName, new Conversation(genConvName, u));
				}
			}
			return success;
		}
		synchronized(conversations) {
			success = !conversations.containsKey(convName);
			if(success)
				conversations.put(convName, new Conversation(convName, u));
		}
		return success;
	}
	
	/**
	 * If there is a Conversation associated with convName, adds the User
	 * corresponding to username to that Conversation and sends an added
	 * to conversation message to to every other client in this conversation,
	 * and sends to the given User a entered conversation message.
	 * 
	 * Fails to add the User to the conversation if username is null, not in users,
	 * already in the conversation, or if there is no Conversation associated
	 * with convName.
	 * 
	 * @param username The name of the user to add.
	 * @param convName The conversation to which to add the User.
	 * @return True if the User was properly added to the conversation.
	 */
	boolean addToConversation(String username, String convName) {
		Conversation conv;
		boolean success = false;
		User u = userByUsername(username);
		if(u == null)
			return false;
		synchronized(conversations) {
			conv = conversations.get(convName);
		}
		if(conv == null) { // No conv associated with convName.
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
		User u = userByUsername(username);
		if(u == null)
			return false;
		synchronized(conversations) {
			conv = conversations.get(convName);
		}
		if(conv == null)
			return false;
		synchronized(conv) {
			if(!conv.contains(u))
				return false;
			conv.remove(u);
		}
		return true;
	}
	
	/**
	 * If there is a conversation associated with convName, sends a participants
	 * message to the client specified by username.  Returns whether or not a
	 * participants message was successfully generated.
	 * 
	 * Fails and returns false if username is null or not in users or if there is
	 * no conversation associated with convName.
	 * 
	 * @param username The name of the User requesting the participants message.
	 * @param convName The name of the conversation of which to get participants.
	 * @return True if a participants message is sent to the client; false otherwise.
	 */
	boolean retrieveParticipants(String username, String convName) {
		Conversation conv;
		User u = userByUsername(username);
		if(u == null)
			return false;
		synchronized(conversations) {
			conv = conversations.get(convName);
		}
		if(conv == null)
			return false;
		u.sendParticipantsMessage(conv.toArray());
		return true;
	}
	
	/**
	 * If username1 and username2 are non-null, refer to Users in this.users,
	 * and are not equal, creates a new Conversation with a unique, auto-generated
	 * name containing only the Users with names username1 and username2 and adds
	 * it to this.conversations.  Sends an entered conversation message to each client,
	 * and returns true upon success.
	 * 
	 * If either username1 or username2 is null or does not refer to a User in
	 * this.users, or if username1.equals(username2), returns false.
	 * 
	 * @param username1 The name of the one user to add to the conversation.
	 * @param username2 The name of the another user to add to the conversation,
	 * 		  different from username1.
	 * @return True if the conversation between the two clients is properly created;
	 * 		   false otherwise.
	 */
	boolean twoWayConversation(String username1, String username2) {
		boolean success = false;
		Conversation conv;
		User u1 = userByUsername(username1);
		User u2 = userByUsername(username2);
		if(u1 == null || u2 == null || u1.equals(u2))
			return false;
		String genConvName = null;
		while(!success) {
			genConvName = String.valueOf(new Random().nextLong());
			synchronized(conversations) {
				success = !conversations.containsKey(genConvName);
				if(success) {
					conv = new Conversation(genConvName, u1, u2);
					conversations.put(genConvName, conv);
				}
			}
		}
		return success;
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
	 * If u is null, returns false.
	 * 
	 * If u.username is not null or the empty string and is already in this.users,
	 * sends a disconnected message to u and returns false.
	 * 
	 * If u.username is null or the empty string, generates a unique username and
	 * sets u.username.  If u.username is null or the empty string or is not already
	 * in this.users, adds u to this.users, sends an initial users list message
	 * to u and a connected message to all other Users in this.users.
	 * 
	 * @param u The User to add.
	 * @return True if u was properly added, false if u is null or
	 * 		   if a User with the same name is already in users.
	 */
	boolean connectUser(User u) {
		boolean added = false;
		Object[] usersArray = new Object[0];
		String username;
		if(u == null)
			return false;
		username = u.getUsername();
		if(username == null || username.equals("")) {
			while(!added) {
				username = "user" + String.valueOf(new Random().nextInt(Integer.MAX_VALUE));
				synchronized(users) {
					added = !users.containsKey(username);
					if(added) {
						u.setUsername(username);
						usersArray = users.values().toArray();
						users.put(username, u);
					}
				}
			}
		}
		synchronized(users) {
			if(!users.containsKey(username)) {
				usersArray = users.values().toArray();
				users.put(username, u);
				added = true;
			}
		}
		if(added) {
			for(Object v : usersArray)
				((User)v).sendConnectedMessage(u);
			u.sendInitUsersListMessage(usersArray);
		}
		else
			u.sendDisconnectedMessage(u);
		return added;
	}
	
	/**
	 * Returns the User in this.users corresponding to the given username.
	 * Returns null if username is null or if username is not in this.users.
	 * 
	 * @param username The username to look up.
	 * @return the User in this.users corresponding to the given username
	 * 		   or null if username is null or if username is not in this.users.
	 */
	private User userByUsername(String username) {
		if(username == null)
			return null;
		synchronized(users) {
			return users.get(username);
		}
	}
	
	/**
	 * Makes the server listen for user connections on the calling thread.  When
	 * a user connection is received over serverSocket, creates and starts an
	 * instance of User, which is a subclass of Thread.  (The User then waits for
	 * a valid connect message before adding itself to this.users.)
	 * 
	 * Blocks until terminated.
	 */
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

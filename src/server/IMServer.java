package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class IMServer implements Runnable {

	private final Set<User> users;
	private final Map<String, Conversation> conversations;
	private final ServerSocket serverSocket;
	
	/**
	 * Creates a new instance of IMServer on the specified port.
	 * Initializes users and conversations to be empty.
	 * 
	 * @param port The port on which to listen for user connections.
	 * @throws IOException If server socket cannot be set up on this port.
	 */
	IMServer(int port) throws IOException {
		users = new HashSet<User>();
		conversations = new HashMap<String, Conversation>();
		serverSocket = new ServerSocket(port);
	}
	
	/**
	 * Sends the message m to all clients in the conversation specified by
	 * convName with sender name u and ID messageId.  The variable messageId
	 * should be unique among all messages sent by this client to the
	 * conversation associated with convName.
	 * 
	 * Fails to send message if u is null, not in users, or not in the
	 * specified conversation or if there is no conversations associated with
	 * convName.
	 * 
	 * @param u The user sending the message.
	 * @param convName The string associated with the conversation to which
	 * 		  to send the message.
	 * @param messageId The message ID, unique among messages sent by u.
	 * @param m The message string.
	 * @return True if the message is properly sent, false otherwise.
	 */
	boolean sendMessage(User u, String convName, int messageId, String m) {
		Conversation conv;
		if(u == null)
			return false;
		synchronized(users) {
			if(!users.contains(u))
				return false;
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
	 * Conversation with name convName containing only u and adds it to
	 * this.conversations.  If convName is null or empty, creates a new
	 * Conversation associated with a unique String containing only u and
	 * adds it to this.conversations.  Sends a new conversation receipt
	 * message to the client represented by u with information on whether
	 * or not the conversation was successfully created.  newConversation
	 * is thread-safe because it synchronizes on conversations to prevent
	 * multiple users from creating new conversations, which could possibly
	 * have the same name, at the same time.
	 * 
	 * Fails to create a new conversation if u is null or not in users or
	 * if convName already refers to a Conversation in conversations.
	 * 
	 * @param u The user creating the conversation
	 * @param convName The name of the conversation to be created, or null
	 * 		  or "" for an auto-generated name.
	 * @return True if the conversation is properly created, false otherwise.
	 */
	boolean newConversation(User u, String convName) {
		if(u == null)
			return false;
		synchronized(users) {
			if(!users.contains(u))
				return false;
		}
		if(convName == null || convName.equals("")) {
			while(true) {
				genConvName = String.valueOf(new Random().nextLong());
				synchronized(conversations) {
					if(!conversations.containsKey(genConvName)) {
						conversations.put(convName, new Conversation(genConvName, u));
						break;
					}
				}
			}
		}
		else {
			synchronized(conversations) {
				if(!conversations.containsKey(convName))
					conversations.put(convName, new Conversation(convName, u));
				else
					return false;
			}
		}
		return true;			
	}
	
	/**
	 * If there is a Conversation associated with convName, adds u to that
	 * Conversation and sends an added to conversation message to to every
	 * other client in this conversation, and sends to u a entered
	 * conversation message.  If there is no Conversation associated with
	 * convName, sends a removed from conversation message to the client
	 * associated with u.
	 * 
	 * Fails to add u to the conversation if u is null, not in users,
	 * already in the conversation, or if there is no Conversation associated
	 * with convName.  In the last case, sends a removed from conversation
	 * message to u.
	 * 
	 * @param u The user to add.
	 * @param convName The conversation to which to add u.
	 * @return True if u was properly added to the conversation.
	 */
	boolean addToConversation(User u, String convName) {
		Conversation conv;
		if(u == null)
			return false;
		synchronized(users) {
			if(!users.contains(u))
				return false;
		}
		synchronized(conversations) {
			conv = conversations.get(convName);
		}
		if(conv == null) { // No conv associated with convName.
			u.removeUserInConversation(u, convName);
			return false;
		}
		synchronized(conv) {
			if(!conv.contains(u))
				conv.add(u);
			else
				return false;
		}
		return true;
	}
	
	/**
	 * If there is a Conversation associated with convName, removes u
	 * from the Conversation, sends a removed from conversation
	 * message to every other User in the Conversation, and removes
	 * the Conversation from this.conversations if the Conversation
	 * contains no Users after removing u.
	 * 
	 * Fails to remove u from the conversation if u is null, not in
	 * users, not in the conversation, or if there is no Conversation
	 * associated with convName in conversations.
	 * 
	 * @param u The user to remove.
	 * @param convName The string corresponding to the conversation
	 * 		  from which to remove u.
	 * @return True if u is properly removed from the conversation,
	 * 		   false otherwise.
	 */
	boolean removeFromConversation(User u, String convName) {
		Conversation conv;
		if(u == null)
			return false;
		synchronized(users) {
			if(!users.contains(u))
				return false;
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
			if(conv.getUsers().isEmpty()) {
				synchronized(conversations) {
					conversations.remove(convName);
				}
			}
		}
		return true;
	}
	
	/**
	 * Removes User u from each of his conversations, sending a
	 * removed from conversation message to every other user in
	 * the conversation, using exitConversation, and removing
	 * empty conversations from this.conversations.  Removes u
	 * from this.users.  Sends a disconnected message to all
	 * clients.  Interrupts u if u is non-null.
	 * 
	 * If u is null, does nothing.
	 * 
	 * @param u The user to disconnect.
	 */
	void disconnectUser(User u) {
		if(u == null)
			return;
		u.disconnect();
		synchronized(users) {
			users.remove(u);
		}
		u.interrupt();
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
	
	/**
	 * Adds u to users if u is non-null.
	 * 
	 * @param u The User to add.
	 * @return True if u was properly added, false if u is null or
	 * 		   if a User with the same name is already in users.
	 */
	boolean connectUser(User u) {
		if(u == null)
			return false;
		synchronized(users) {
			return users.add(u);
		}
	}
}

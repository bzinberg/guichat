package server;

import java.util.HashSet;
import java.util.Set;

/**
 * Conversations in guichat are represented by instances of Conversation.
 * Instances of Conversation have the following instance variables:
 * 
 *  - users is a set of Users that represents the clients currently participating
 *    in the conversation
 *  - name is the name of the conversation as a String.  name is globally unique
 *    among all conversations in any IMServer, and is the String corresponding to
 *    the conversation in the conversations map in IMServer.
 * 
 * See the Conversation section in the design document for more information on
 * the Conversation class.
 */
public class Conversation {
	
	private final String name;
	private final Set<User> users;
	
	/**
	 * Creates an instance of Conversation with the given name.
	 * Sets this.name to name and initializes this.users.
	 * 
	 * @param name The name of this Conversation.
	 */
	Conversation(String name) {
		this.name = name;
		users = new HashSet<User>();
	}
	
	/**
	 * Creates an instance of Conversation with the given name, containing
	 * a given User.  Sets this.name to name, initializes this.users, adds
	 * u to this.users, and tells u to add this to its set of conversations
	 * (which causes an entered conversation message to be sent to u).
	 * 
	 * @param name The name of this Conversation.
	 * @param u The User to be added to this Conversation.
	 */
	Conversation(String name, User u) {
		this(name);
		users.add(u);
		u.addConversation(this);
	}
	
	/**
	 * Creates an instance of Conversation with the given name, containing
	 * two given Users.  Sets this.name to name, initializes this.users, adds
	 * u1 and u2 to this.users, and tells u1 and u2 to add this to their sets
	 * of conversations (which causes entered conversation messages to be sent
	 * to u1 and to u2).
	 * 
	 * Requires that u1 is not equal to u2.
	 * 
	 * @param name The name of this Conversation.
	 * @param u1 One User to be added to this Conversation.
	 * @param u2 Another User to be added to this Conversation, different from u1.
	 */
	Conversation(String name, User u1, User u2) {
		this(name);
		if(u1 == u2 || (u1 != null && u1.equals(u2)))
			return;
		users.add(u1);
		users.add(u2);
		u1.addConversation(this);
		u2.addConversation(this);
	}
	
	/**
	 * If u is non-null and this.users does not already contain u, adds u to this.users,
	 * sends an added to conversation message to every other client in this conversation,
	 * and sends to u a entered conversation message.  Returns whether or not this.users
	 * changed as a result of the call to add.
	 * 
	 * @param u The User to add to this Conversation.
	 * @return True if u is added to this.users; false otherwise.
	 */
	synchronized boolean add(User u) {
		if(u == null) return false;
		boolean b = users.add(u);
		if (!b) return false;
		u.addConversation(this);
		for (User v : users) {
			if (!v.equals(u))
				v.sendAddedToConvMessage(u, name);
		}
		return true;
	}
	
	/**
	 * If u is non-null and this.users contains u, removes u from this.users and sends
	 * a removed from conversation message to every other client in this conversation.
	 * Returns whether or not this.users changed as a result of the call to remove.
	 * 
	 * @param u The User to remove from this Conversation.
	 * @return True if u is removed from this.users; false otherwise.
	 */
	synchronized boolean remove(User u) {
		if(u == null) return false;
		boolean b = users.remove(u);
		if (!b) return false;
		for (User v : users) {
			if (!v.equals(u))
				v.sendRemovedFromConvMessage(u, name);
		}
		u.removeConversation(this);
		return true;
	}
	
	/**.
	 * Returns whether or not this.users contains u.
	 * 
	 * @param u The User to find in this.users.
	 * @return True if u is in this.users; false otherwise.
	 */
	synchronized boolean contains(User u) {
		return users.contains(u);
	}
	
	/**
	 * Sends to every User in this.users a message with the given sender,
	 * message text, and message ID.
	 * 
	 * @param u The sending user, non-null.
	 * @param m The message text.
	 * @param messageId The message ID of this message, unique among messages sent
	 * 		  by u.
	 */
	synchronized void sendMessage(User u, String m, int messageId) {
		for (User v : users) {
			v.sendIMMessage(u, m, messageId, name);
		}
	}
	
	/**
	 * Returns an array representation of this.users.
	 * 
	 * @return An array representation of this.users.
	 */
	synchronized Object[] toArray() {
		return users.toArray();
	}
	
	/**
	 * Accessor method for this.name.
	 * 
	 * @return this.name, the name of this Conversation.
	 */
	String getName() {
		return name;
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

	/**
	 * Returns as a String the conversation name and the names of the Users in
	 * this.users, according to ENTERED_CONV_CONTENT in the server-to-client network
	 * described in the Network Protocol section of the design document.
	 * 
	 * @return A (non-null) String representation of this.
	 */
	@Override
	public synchronized String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(name);
		for (User u : users) {
			ret.append("\t");
			ret.append(u.getUsername());
		}
		return ret.toString();
	}
	
}

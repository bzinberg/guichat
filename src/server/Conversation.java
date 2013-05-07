package server;

import java.util.HashSet;
import java.util.List;

public class Conversation {
	
	private final String name;
	private final HashSet<User> userSet;
	
	/**
	 * Creates a conversation with a given name
	 * @param name
	 */
	Conversation(String name) {
		this.name = name;
		userSet = new HashSet<User>();
	}
	
	/**
	 * Creates a conversation with a given name
	 * and user
	 * @param name
	 * @param u
	 */
	Conversation(String name, User u) {
		this(name);
		addUser(u);
	}
	
	/**
	 * adds user u to the conversation. Notifies the user with the set of
	 * users in the conversation. Notifies other users that the user has been
	 * added to the conversation.
	 * @param u
	 * @return true iff there is no user in the conversation with same id
	 */
	synchronized boolean addUser(User u) {
		boolean b = userSet.add(u);
		if (b) {
			u.sendConversation(this);
			for (User v : userSet) {
				if (!v.equals(u))
					v.addedUserToConversation(u, this);
			}
		}
		return b;
	}
	
	/**
	 * removes user u from the conversation. Notifes every user that the user
	 * has been removed from the conversation.
	 * @param u
	 * @return true iff there is a user in the conversation with the same id
	 */
	synchronized boolean removeUser(User u) {
		boolean b = userSet.remove(u);
		if (b) {
			for (User v : userSet) {
				v.removedFromConversation(u, this);
			}
		}
		u.removedFromConversation(u, this);
	}
	
	/**.
	 * @param u
	 * @return true iff there is a user in the conversation with the same id
	 */
	synchronized boolean containsUser(User u) {
		return userSet.contains(u);
	}
	
	/**
	 * Sends every user in the conversation with the message, sender, id, conversation
	 * @param u
	 * @param m
	 * @param uniqueID
	 */
	synchronized void sendMessage(User u, String m, int uniqueID) {
		for (User v : userSet) {
			v.sendMessage(u, m, uniqueID, this);
		}
	}
	
	/**
	 * @return name of the conversation
	 */
	String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}
	
}

package server;

import java.util.HashSet;
import java.util.Set;

public class Conversation {
	
	private final String name;
	private final Set<User> users;
	
	/**
	 * Creates a conversation with a given name
	 * @param name
	 */
	Conversation(String name) {
		this.name = name;
		users = new HashSet<User>();
	}
	
	/**
	 * Creates a conversation with a given name
	 * and user
	 * @param name
	 * @param u
	 */
	Conversation(String name, User u) {
		this(name);
		add(u);
	}
	
	/**
	 * adds user u to the conversation. Notifies the user with the set of
	 * users in the conversation. Notifies other users that the user has been
	 * added to the conversation.
	 * @param u
	 * @return true iff there is no user in the conversation with same name already
	 */
	synchronized boolean add(User u) {
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
	 * removes user u from the conversation. Notifes every user that the user
	 * has been removed from the conversation.
	 * @param u
	 * @return true iff there is a user in the conversation with the same name
	 */
	synchronized boolean remove(User u) {
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
	 * @param u
	 * @return true iff there is a user in the conversation with the same id
	 */
	synchronized boolean contains(User u) {
		return users.contains(u);
	}
	
	/**
	 * Sends every user in the conversation with the message, sender, id, conversation
	 * @param u
	 * @param m
	 * @param uniqueID
	 */
	synchronized void sendMessage(User u, String m, int uniqueID) {
		for (User v : users) {
			v.sendIMMessage(u, m, uniqueID, name);
		}
	}
	
	synchronized boolean isEmpty() {
		return users.isEmpty();
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

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append(name);
		for (User u : users) {
			ret.append("\t");
			ret.append(u.getUsername());
		}
		return ret.toString();
	}
	
}

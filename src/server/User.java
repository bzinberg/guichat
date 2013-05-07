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
	 */
	User(IMServer server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.convSet = new HashSet<Conversation>();
		this.out = new PrintWriter(socket.getOutputStream(), true);
		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	
	private void send(String s) {
		out.println(s);
		out.flush();
	}
	
	private void close() {
		in.close();
		out.close();
	}
	
	@Override
	public void run() {
		try {
            for (String line = in.readLine(); line != null; line = in.readLine())
            	handleRequest(line);
		} catch(Exception e) {
			
			try { in.close(); }
			catch(IOException e) {}
			out.close();
		}
	}
	
	/**
	 * Handles the given request and returns true.  If the request
	 * does not follow the specified grammar or cannot be processed,
	 * returns false.
	 * 
	 * @param req The client's request.
	 * @return True if the request is valid and is properly executed.
	 */
	private boolean handleRequest(String req) {
		if(req == null)
			return false;
		String[] args = req.split("\t");
		if(args.length == 0)
			return false;
		if(args[0].equals(NetworkConstants.CONNECT)) // CONNECT
			return connect(args);
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
		else if(args[0].equals(NetworkConstants.DISCONNECT))
			return disconnect(args);
		else
			return false;
	}
	
	private boolean connect(String[] args) {
		if(args.length != 2)
			return false;
		if(!args[1].matches("[^\t\n]{1,256}"))
			return false;
		if()
	}
	
	void synchronized disconnect() {
		for(Conversation c : convSet) {
			c.remove(u);
		}
		
	}
	
	boolean addConversation(Conversation conv) {
		boolean b = convSet.add(conv);
		if (!b) return false;
		String message = NetworkConstants.ENTERED_CONV + "\t" + conv.toString();
		send(message);
		return true;
	}
	
	void addUserInConversation(User u, String convName) {
		String message = NetworkConstants.ADDED_TO_CONV + "\t" + convName;
		send(message);
	}
	
	void removeUserInConversation(User u, String convName) {
		String message = NetworkConstants.REMOVED_FROM_CONV + "\t" + convName;
		send(message);
	}
	
	boolean removeConversation(Conversation conv) {
		boolean b = convSet.remove(conv);
		return b;
	}
	
	String getUsername() {
		return name;
	}
	
	@Override
	boolean equals(Object o) {
		if ()
	}
	
	boolean isInConversation(Conversation conv) {
		return convSet.contains(conv);
	}

}
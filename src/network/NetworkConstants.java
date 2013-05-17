package network;

/**
 * Contains constants involved in network communications.
 *
 */
public class NetworkConstants {
	
	public static final int DEFAULT_PORT = 4445;
	
	// Server to client messages.
	
	public static final String INIT_USERS_LIST = "0";
	public static final String IM = "1";
	public static final String ADDED_TO_CONV = "2";
	public static final String ENTERED_CONV = "3";
	public static final String REMOVED_FROM_CONV = "4";
	public static final String CONNECTED = "5";
	public static final String DISCONNECTED = "6";
	public static final String PARTICIPANTS = "7";
	public static final String ERROR = "8";
	
	// Client to server messages.
	
	public static final String CONNECT = "0";
	//public static final String IM = "1";
	public static final String NEW_CONV = "2";
	public static final String ADD_TO_CONV = "3";
	public static final String ENTER_CONV = "4";
	public static final String EXIT_CONV = "5";
	public static final String DISCONNECT_NO_TAB = "6";
	public static final String DISCONNECT = "6\t";
	public static final String RETRIEVE_PARTICIPANTS = "7";
	public static final String TWO_WAY_CONV = "8";

	// Regular expressions for portions of network messages.
	
	public static final String USERNAME = "[^\t\n]{1,256}";
	public static final String NEW_USERNAME = "[^\t\n]{0,256}";
	public static final String CONV_NAME = "[^\t\n]{1,256}";
	public static final String NEW_CONV_NAME = "[^\t\n]{0,256}";
	public static final String IM_ID = "[0-9]{1,9}";
	public static final String MESSAGE = "[^\t\n]{1,512}";
	
}

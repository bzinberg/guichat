package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.junit.Test;

import test.TestClient;

import network.NetworkConstants;

/**
 * Tests for the methods in User that are independent of User.server.
 * Tests that require an IMServer (i.e., the private helper methods for run)
 * are located in ServerTest. 
 * 
 */
public class UserTest {
	
	// sending messages
	
	/**
	 * Expect that addConversation sends an entered conversation message
	 * and returns true when the conversation is not already in User.conversations.
	 */
	@Test(timeout=1000) public void addNewConversationTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			assertTrue(u.addConversation(conv));
			assertEquals(NetworkConstants.ENTERED_CONV + "\tconv", util.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that addConversation returns false when the conversation
	 * is already in User.conversations.
	 */
	@Test public void addExistingConversationTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.addConversation(conv);
			assertFalse(u.addConversation(conv));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that removeConversation returns false when the conversation
	 * is not already in User.conversations.
	 */
	@Test public void removeNewConversationTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			assertFalse(u.removeConversation(conv));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that removeConversation returns true when the conversation has been
	 * added to User.conversations.
	 */
	@Test public void removeExistingConversationTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.addConversation(conv);
			assertTrue(u.removeConversation(conv));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that getUsername returns null when the username is unset.
	 */
	@Test public void unsetUsernameTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			assertEquals(null, u.getUsername());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that setUsername properly sets the username and is retrieved by
	 * getUsername.  (The preconditions for setUsername ensure that their is only
	 * one case and no edge cases.)
	 */
	@Test public void setUsernameTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			assertEquals("user", u.getUsername());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that sendInitUsersListMessage properly sends an initial users list message,
	 * following the grammar, when given an empty array.
	 */
	@Test(timeout=1000) public void sendInitUsersListMessageEmptyUsersTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendInitUsersListMessage(new Object[0]);
			assertEquals(NetworkConstants.INIT_USERS_LIST + "\tuser", util.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that sendInitUsersListMessage properly sends an initial users list message,
	 * following the grammar, with the calling User's name first, when given a nonempty
	 * array.
	 */
	@Test(timeout=1000) public void sendInitUsersListMessageNonEmptyUsersTest() {
		ServerSocket serverSocket = null;
		TestClient utils[] = new TestClient[5];
		Object[] users = new User[5];
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			for(int i = 0; i < utils.length; ++i)
				utils[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
			for(int i = 0; i < users.length; ++i) {
				users[i] = new User(null, serverSocket.accept());
				((User)users[i]).setUsername("user" + i);
			}
			((User)users[0]).sendInitUsersListMessage(users);
			StringBuilder expected = new StringBuilder(NetworkConstants.INIT_USERS_LIST + "\tuser0");
			for(int i = 0; i < users.length; ++i)
				expected.append("\t" + NetworkConstants.USERNAME);
			assertTrue(utils[0].readLine().matches(expected.toString()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				for(TestClient util : utils) {
					if(util != null)
						util.close();
				}
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that sendEnteredConvMessage properly sends an entered conversation message,
	 * following the grammar.
	 */
	@Test(timeout=1000) public void sendEnteredConvMessageTest() {
		ServerSocket serverSocket = null;
		TestClient util = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			Conversation conv = new Conversation("conv", u);
			u.sendEnteredConvMessage(conv);
			assertEquals(NetworkConstants.ENTERED_CONV + "\tconv\tuser", util.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(util != null)
					util.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}

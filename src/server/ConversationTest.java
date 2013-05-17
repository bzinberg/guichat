package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import network.NetworkConstants;

import org.junit.Test;


/**
 * 	JUnit Tests for the Conversation class.
 * 
 *  @category no_didit
 */
public class ConversationTest {
	
	/**
	 * Check that constructor with no initial users and toString work.
	 */
	@Test
	public void noUserConstructorTest() {
		Conversation conv = new Conversation("conv");
		assertEquals("conv", conv.toString());
	}

	/**
	 * Check that constructor with one initial user and toString work.
	 */
	@Test(timeout=1000)
	public void oneUserConstructorTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv", u);
			assertEquals("conv\tuser", conv.toString());
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Check that constructor with one initial user and toString work.
	 */
	@Test(timeout=2000)
	public void twoUserConstructorTest() {
		ServerSocket serverSocket = null;
		Socket socket1 = null, socket2 = null;
		User u = null, v = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket1 = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket1);
			socket2 = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			v = new User(null, socket2);
			u.setUsername("user1");
			v.setUsername("user2");
			Conversation conv = new Conversation("conv", u, v);
			assertEquals("conv\tuser2\tuser1", conv.toString());
			u.interrupt();
			v.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket1 != null)
					socket1.close();
				if(socket2 != null)
					socket2.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			assertTrue(false);
		}
	}
	
	/**
	 * Expect that add returns true when a User is added for the first time.
	 */
	@Test(timeout=1000)
	public void addFirstTimeTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv");
			assertTrue(conv.add(u));
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that add returns false when a User is added for the second time.
	 */
	@Test(timeout=1000)
	public void addSecondTimeTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv");
			conv.add(u);
			assertFalse(conv.add(u));
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that add returns false when a null User is added.
	 */
	@Test
	public void addNullTest() {
		Conversation conv = new Conversation("conv");
		assertFalse(conv.add(null));
	}

	/**
	 * Expect that remove returns false when a User not in a conversation is removed.
	 */
	@Test(timeout=1000)
	public void removeNewUserTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv");
			assertFalse(conv.remove(u));
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that remove returns true when a User in a conversation is removed.
	 */
	@Test(timeout=1000)
	public void removeExistingUserTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv", u);
			assertTrue(conv.remove(u));
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that remove returns false when a null User is removed.
	 */
	@Test
	public void removeNullTest() {
		Conversation conv = new Conversation("conv");
		assertFalse(conv.remove(null));
	}
	
	/**
	 * Expect that contains returns false when a User is not in the conversation.
	 */
	@Test(timeout=1000)
	public void containsNewUserTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv");
			assertFalse(conv.contains(u));
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that contains returns true when a User is in the conversation.
	 */
	@Test(timeout=1000)
	public void containsExistingUserTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv", u);
			assertTrue(conv.contains(u));
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that contains returns false when given null.
	 */
	@Test
	public void containsNullTest() {
		Conversation conv = new Conversation("conv");
		assertFalse(conv.contains(null));
	}
	
	/**
	 * Expect that getName correctly returns the conversation's name.
	 * Since this.name is non-null, there is only one case.
	 */
	@Test
	public void getNameTest() {
		Conversation conv = new Conversation("conv");
		assertEquals("conv", conv.getName());
	}

	/**
	 * Expect that toArray returns an empty array when conversation is empty.
	 */
	@Test
	public void emptyToArrayTest() {
		Conversation conv = new Conversation("conv");
		assertEquals(0, conv.toArray().length);
	}
	
	/**
	 * Expect that toArray returns an an array of users when non-empty.
	 */
	@Test(timeout=1000)
	public void nonEmptyToArrayTest() {
		ServerSocket serverSocket = null;
		Socket socket = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			u.setUsername("user");
			Conversation conv = new Conversation("conv", u);
			Object[] users = conv.toArray();
			assertEquals(1, users.length);
			assertEquals(u, users[0]);
			u.interrupt();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			try {
				if(socket != null)
				socket.close();
				if(serverSocket != null)
				serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that hashCode returns the Conversation's name's hashCode.
	 */
	@Test(timeout=1000) public void hashcodeTest() {
		Conversation conv = new Conversation("conv");
		assertEquals("conv".hashCode(), conv.hashCode());
	}

}

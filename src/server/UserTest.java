package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.UnknownHostException;

import org.junit.Test;

import test.TestClient;

import network.NetworkConstants;

/**
 * JUnit Tests for the methods in User that are independent of User.server.
 * Tests that require an IMServer (i.e., the private helper methods for run)
 * are located in ServerTest. 
 * 
 * @category no_didit
 */
public class UserTest {
	
	/**
	 * Expect that addConversation sends an entered conversation message
	 * and returns true when the conversation is not already in User.conversations.
	 */
	@Test(timeout=1000) public void addNewConversationTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			assertTrue(u.addConversation(conv));
			assertEquals(NetworkConstants.ENTERED_CONV + "\tconv", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
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
		TestClient client = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
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
				if(client != null)
					client.close();
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
		TestClient client = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
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
				if(client != null)
					client.close();
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
		TestClient client = null;
		Conversation conv = new Conversation("conv");
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
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
				if(client != null)
					client.close();
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
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
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
				if(client != null)
					client.close();
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
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
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
				if(client != null)
					client.close();
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
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendInitUsersListMessage(new Object[0]);
			assertEquals(NetworkConstants.INIT_USERS_LIST + "\tuser", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
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
		TestClient clients[] = new TestClient[5];
		Object[] users = new User[5];
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			for(int i = 0; i < clients.length; ++i)
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
			for(int i = 0; i < users.length; ++i) {
				users[i] = new User(null, serverSocket.accept());
				((User)users[i]).setUsername("user" + i);
			}
			StringBuilder expected = new StringBuilder(NetworkConstants.INIT_USERS_LIST + "\tuser0");
			for(int i = 1; i < users.length; ++i)
				expected.append("\t" + NetworkConstants.USERNAME);
			Object[] otherUsers = new Object[users.length - 1];
			for(int i = 0; i < users.length - 1; ++i)
				otherUsers[i] = users[i+1];
			((User)users[0]).sendInitUsersListMessage(otherUsers);
			assertTrue(clients[0].readLine().matches(expected.toString()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				for(TestClient client : clients) {
					if(client != null)
						client.close();
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
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			Conversation conv = new Conversation("conv", u);
			u.sendEnteredConvMessage(conv);
			assertEquals(NetworkConstants.ENTERED_CONV + "\tconv\tuser", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that sendAddedToConvMessage properly sends an added to conversation message,
	 * following the grammar.
	 */
	@Test(timeout=1000) public void sendAddedToConvMessageTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendAddedToConvMessage(u, "conv");
			assertEquals(NetworkConstants.ADDED_TO_CONV + "\tuser\tconv", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that sendRemovedFromConvMessage properly sends an removed from conversation message,
	 * following the grammar.
	 */
	@Test(timeout=1000) public void sendRemovedFromConvMessageTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendRemovedFromConvMessage(u, "conv");
			assertEquals(NetworkConstants.REMOVED_FROM_CONV + "\tuser\tconv", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that sendConnectedMessage properly sends a connected message,
	 * following the grammar.
	 */
	@Test(timeout=1000) public void sendConnectedMessageTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendConnectedMessage(u);
			assertEquals(NetworkConstants.CONNECTED + "\tuser", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that sendDisonnectedMessage properly sends a disconnected message,
	 * following the grammar.
	 */
	@Test(timeout=1000) public void sendDisconnectedMessageTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendDisconnectedMessage(u);
			assertEquals(NetworkConstants.DISCONNECTED + "\tuser", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that sendIMMessage properly sends an IM message,
	 * following the grammar.
	 */
	@Test(timeout=1000) public void sendIMMessageTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendIMMessage(u, "message", 4354, "conv");
			assertEquals(NetworkConstants.IM + "\tuser\tconv\t4354\tmessage", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that sendParticipantsMessage properly sends a participants message,
	 * following the grammar, when given an empty array.
	 */
	@Test(timeout=1000) public void sendParticipantsMessageEmptyUsersTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendParticipantsMessage("conv", new Object[0]);
			assertEquals(NetworkConstants.PARTICIPANTS + "\tconv", client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that sendParticipantsMessage properly sends a participants message when given
	 * a nonempty array, according to the network protocol grammar.
	 */
	@Test(timeout=1000) public void sendParticipantsMessageNonEmptyUsersTest() {
		ServerSocket serverSocket = null;
		TestClient clients[] = new TestClient[5];
		Object[] users = new User[5];
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			for(int i = 0; i < clients.length; ++i)
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
			for(int i = 0; i < users.length; ++i) {
				users[i] = new User(null, serverSocket.accept());
				((User)users[i]).setUsername("user" + i);
			}
			((User)users[0]).sendParticipantsMessage("conv", users);
			StringBuilder expected = new StringBuilder(NetworkConstants.PARTICIPANTS + "\tconv");
			for(int i = 0; i < users.length; ++i)
				expected.append("\t" + NetworkConstants.USERNAME);
			assertTrue(clients[0].readLine().matches(expected.toString()));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				for(TestClient client : clients) {
					if(client != null)
						client.close();
				}
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that sendErrorMessage properly sends an error message, according to the
	 * network protocol grammar.
	 */
	@Test(timeout=1000) public void sendErrorMessageTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			u.sendErrorMessage("This message was bad and triggered an error.\t40394\tiwn91n[");
			assertEquals(NetworkConstants.ERROR +
					"\tThis message was bad and triggered an error.\t40394\tiwn91n[",
					client.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that equals returns false when argument is null.
	 */
	@Test(timeout=1000) public void nullEqualsTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			assertFalse(u.equals(null));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Expect that equals returns false when argument is not of type User.
	 */
	@Test(timeout=1000) public void notUserEqualsTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u1 = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u1 = new User(null, serverSocket.accept());
			u1.setUsername("user");
			Object u2 = new Object();
			assertFalse(u1.equals(u2));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	

	/**
	 * Expect that equals returns false when argument has a different name.
	 */
	@Test(timeout=1000) public void differentNameEqualsTest() {
		ServerSocket serverSocket = null;
		TestClient client1 = null, client2 = null;
		User u1 = null, u2 = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			u1 = new User(null, serverSocket.accept());
			u1.setUsername("user1");
			u2 = new User(null, serverSocket.accept());
			u2.setUsername("user2");
			assertFalse(u1.equals(u2));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client1 != null)
					client1.close();
				if(client2 != null)
					client2.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that equals returns true when argument has the same name.
	 */
	@Test(timeout=1000) public void sameNameEqualsTest() {
		ServerSocket serverSocket = null;
		TestClient client1 = null, client2 = null;
		User u1 = null, u2 = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			u1 = new User(null, serverSocket.accept());
			u1.setUsername("user");
			u2 = new User(null, serverSocket.accept());
			u2.setUsername("user");
			assertTrue(u1.equals(u2));
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client1 != null)
					client1.close();
				if(client2 != null)
					client2.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that hashCode returns 0 when the user's name is not set.
	 */
	@Test(timeout=1000) public void noNameHashcodeTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			assertEquals(0, u.hashCode());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Expect that hashCode returns the User's name's hashCode when the user's
	 * name is set.
	 */
	@Test(timeout=1000) public void namedHashcodeTest() {
		ServerSocket serverSocket = null;
		TestClient client = null;
		User u = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			u = new User(null, serverSocket.accept());
			u.setUsername("user");
			assertEquals("user".hashCode(), u.hashCode());
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertFalse(true);
		} catch (IOException e) {
			e.printStackTrace();
			assertFalse(true);
		} finally {
			try {
				if(client != null)
					client.close();
				if(serverSocket != null)
					serverSocket.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	

}

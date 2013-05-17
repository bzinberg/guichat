package server;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import test.TestClient;
import network.NetworkConstants;

/**
 * JUnit tests for the server. Sends messages to the server and checks for the
 * correct responses.
 * 
 * @category no_didit
 */
public class ServerTest {
	
	// The amount of time to wait before assuming a network message has been delivered
	// For the purpose of testing only.  This allows us to test certain sequences of messages.
	private static final int EXPECTED_NETWORK_DELAY = 200;
	
	static void waitForNetworkMessage() {
		long time = System.currentTimeMillis(); // Wait to make sure other user is connected.
		while(System.currentTimeMillis() - time < EXPECTED_NETWORK_DELAY);
	}
		
	/**
	 * Ensure that correct message is returned from the server when one user
	 * logs in.
	 */
	@Test(timeout=2000) public void oneUserLoginTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\ta");
			assertEquals(NetworkConstants.INIT_USERS_LIST + "\ta", client.readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Ensure that disconnected message is returned from the server when a user
	 * logs in with a name that is already taken.
	 */
	@Test(timeout=2000) public void nameTakenLoginTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client1.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client2.send(NetworkConstants.CONNECT + "\tuser");
			assertEquals(NetworkConstants.DISCONNECTED + "\tuser", client2.readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}

	/**
	 * Ensure that correct message is returned from the server when a user
	 * logs in after other users are logged in.
	 */
	@Test(timeout=2000) public void multipleUserLoginTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 1; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0] = new TestClient(NetworkConstants.DEFAULT_PORT);
			clients[0].send(NetworkConstants.CONNECT + "\tuser0");
			StringBuilder expected = new StringBuilder(NetworkConstants.INIT_USERS_LIST + "\tuser0");
			for(int i = 1; i < clients.length; ++i)
				expected.append("\t" + NetworkConstants.USERNAME);
			assertTrue(clients[0].readLine().matches(expected.toString()));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Ensure that correct message is returned from the server when a lot
	 * of users are logged in.
	 */
	@Test(timeout=60000) public void largeNumberOfUsersLoginTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[500];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 1; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0] = new TestClient(NetworkConstants.DEFAULT_PORT);
			clients[0].send(NetworkConstants.CONNECT + "\tuser0");
			StringBuilder expected = new StringBuilder(NetworkConstants.INIT_USERS_LIST + "\tuser0");
			for(int i = 1; i < clients.length; ++i)
				expected.append("\t" + NetworkConstants.USERNAME);
			assertTrue(clients[0].readLine().matches(expected.toString()));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}

	/**
	 * Expect IM message to be sent to all clients in a conversation when the sender of
	 * an IM message is in the conversation and other clients are also in the conversation.
	 */
	@Test(timeout=10000) public void inConvMultipleUserIMTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 1; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv");
			String[] line = new String[clients.length];
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.IM + "\tconv\t394\tmessage");
			for(int i = 0; i < clients.length; ++i) {
				do {
					line[i] = clients[i].readLine();
				} while(line[i].startsWith(NetworkConstants.ADDED_TO_CONV)
						|| line[i].startsWith(NetworkConstants.CONNECTED)
						|| line[i].startsWith(NetworkConstants.INIT_USERS_LIST)
						|| line[i].startsWith(NetworkConstants.ENTERED_CONV));
			}
			String expected = NetworkConstants.IM + "\tuser0\tconv\t394\tmessage";
			for(int i = 0; i < clients.length; ++i)
				assertEquals(expected, line[i]);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect IM message to be sent to the sender when the sender of an IM message
	 * is in the conversation but no other clients are in the conversation.
	 */
	@Test(timeout=2000) public void inConvOneUserIMTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client.send(NetworkConstants.IM + "\tconv\t394\tmessage");
			String line;
			do {
				line = client.readLine();
			} while(line.startsWith(NetworkConstants.INIT_USERS_LIST)
					|| line.startsWith(NetworkConstants.ENTERED_CONV));
			String expected = NetworkConstants.IM + "\tuser\tconv\t394\tmessage";
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect error message to be sent to sending client when the sender of an IM message
	 * is not in the conversation but is connected to the server.
	 */
	@Test(timeout=2000) public void notInConvIMTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[1].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 2; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv");
			String line;
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.IM + "\tconv\t394\tmessage");
			do {
				line = clients[0].readLine();
			} while(line.startsWith(NetworkConstants.CONNECTED)
					|| line.startsWith(NetworkConstants.INIT_USERS_LIST));
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.IM + "\tconv\t394\tmessage";
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect error message to be sent to sending client when the sender of an IM message
	 * is not in the conversation or connected to the server.
	 */
	@Test(timeout=2000) public void notConnectedMultipleUserIMTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 1; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			clients[0] = new TestClient(NetworkConstants.DEFAULT_PORT);
			waitForNetworkMessage();
			clients[1].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 2; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv");
			String line;
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.IM + "\tconv\t394\tmessage");
			do {
				line = clients[0].readLine();
			} while(line.startsWith(NetworkConstants.CONNECTED)
					|| line.startsWith(NetworkConstants.INIT_USERS_LIST));
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.IM + "\tconv\t394\tmessage";
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect error message to be sent to sending client when an IM message
	 * is sent to a conversation that doesn't exist.
	 */
	@Test(timeout=2000) public void convDoesNotExistIMTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.IM + "\tconv\t394\tmessage");
			String line;
			do {
				line = client.readLine();
			} while(line.startsWith(NetworkConstants.INIT_USERS_LIST));
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.IM + "\tconv\t394\tmessage";
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect enter conversation message to be sent to the sender of a new conversation message
	 * when the conversation name is unused and the sender is connected to the server.
	 */
	@Test(timeout=2000) public void unusedNameAndConnectedNewConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.NEW_CONV + "\tconv");
			String line;
			do {
				line = client.readLine();
			} while(line.startsWith(NetworkConstants.INIT_USERS_LIST));
			String expected = NetworkConstants.ENTERED_CONV + "\tconv\tuser";
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect no message to be sent to the sender of a new conversation message
	 * when the conversation name is used.
	 */
	@Test(timeout=2000) public void usedNameNewConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.NEW_CONV + "\tconv");
			client.readLine(); // init users list
			client.readLine(); // entered conv
			client.send(NetworkConstants.NEW_CONV + "\tconv"); // Now it's used, so try creating it again.
			waitForNetworkMessage();
			assertFalse(client.ready()); // Should not receive anything.
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect a conversation name to be autogenerated when a user sends a new conversation
	 * message with an empty name.
	 */
	@Test(timeout=2000) public void unspecifiedNameNewConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.NEW_CONV + "\t");
			String line;
			do {
				line = client.readLine();
			} while(line.startsWith(NetworkConstants.INIT_USERS_LIST));
			String expected = NetworkConstants.ENTERED_CONV + "\t" +
					NetworkConstants.CONV_NAME + "\tuser";
			assertTrue(line.matches(expected));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}

	
	/**
	 * Expect an error message to be sent to the sender of a new conversation message
	 * when the conversation name is unused and the sender is not connected to the server.
	 */
	@Test(timeout=2000) public void unusedNameAndDisconnectedNewConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			client.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.NEW_CONV + "\tconv";
			assertEquals(expected, client.readLine()); // Should not receive anything.
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect enter conversation message to be sent to a user and an added to conversation
	 * message to every other user when a user is added to a conversation by a user who is
	 * already in the conversation.
	 */
	@Test(timeout=10000) public void inConvAndConnectedAndUserExistsAndIsNotInConvAddToConvTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[1].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 2; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv");
			String[] line = new String[clients.length];
			waitForNetworkMessage();
			clients[1].send(NetworkConstants.ADD_TO_CONV + "\tuser0\tconv"); // add user0 to conv.
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					line[i] = clients[i].readLine();
			}
			String expectedForOthers = NetworkConstants.ADDED_TO_CONV + "\tuser0\tconv";
			StringBuilder expectedForAddedUser = new StringBuilder(NetworkConstants.ENTERED_CONV + "\tconv");
			for(int i = 0; i < clients.length; ++i)
				expectedForAddedUser.append("\t" + NetworkConstants.USERNAME);
			assertTrue(line[0].matches(expectedForAddedUser.toString())); // entered conv expected
			for(int i = 1; i < clients.length; ++i)
				assertEquals(expectedForOthers, line[i]);
			
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect an entered conversation message to be sent when a user is added to a conversation
	 * by a user who is already in the conversation if the user he adds is also already in the
	 * conversation.  Expect no added to conversation messages to be sent to other users.
	 */
	@Test(timeout=10000) public void inConvAndConnectedAndUserExistsAndIsInConvAddToConvTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 1; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv"); // Everyone in conv
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					clients[i].readLine();
			}
			clients[1].send(NetworkConstants.ADD_TO_CONV + "\tuser0\tconv"); // add user0 to conv
			StringBuilder expectedForAddedUser = new StringBuilder(NetworkConstants.ENTERED_CONV + "\tconv");
			for(int i = 0; i < clients.length; ++i)
				expectedForAddedUser.append("\t" + NetworkConstants.USERNAME);
			assertTrue(clients[0].readLine().matches(expectedForAddedUser.toString())); // entered conv expected
			waitForNetworkMessage();
			for(int i = 1; i < clients.length; ++i)
				assertFalse(clients[i].ready());
			
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect an error message to be sent when a non-existent user is added to a conversation
	 * by a user who is already in the conversation.
	 */
	@Test(timeout=10000) public void inConvAndConnectedAndUserDoesNotExistAddToConvTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 1; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv"); // Everyone in conv
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					clients[i].readLine();
			}
			clients[0].send(NetworkConstants.ADD_TO_CONV + "\tnonexistentuser\tconv");
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.ADD_TO_CONV + "\tnonexistentuser\tconv";
			assertEquals(expected, clients[0].readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}

	/**
	 * Expect an entered conversation message to be sent when a user is added to a conversation
	 * by a user who is not already in the conversation but is connected to the server.  Expect
	 * an added to conversation message to be sent to everyone else in the conversation.
	 */
	@Test(timeout=2000) public void notInConvAndConnectedAddToConvTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[2].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 3; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv"); // user0, user1 not in conv
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					clients[i].readLine();
			}
			clients[1].send(NetworkConstants.ADD_TO_CONV + "\tuser0\tconv"); // add user0 to conv
			String expectedForOthers = NetworkConstants.ADDED_TO_CONV + "\tuser0\tconv";
			StringBuilder expectedForAddedUser = new StringBuilder(NetworkConstants.ENTERED_CONV + "\tconv");
			for(int i = 1; i < clients.length; ++i)
				expectedForAddedUser.append("\t" + NetworkConstants.USERNAME);
			assertTrue(clients[0].readLine().matches(expectedForAddedUser.toString())); // entered conv expected
			for(int i = 2; i < clients.length; ++i)
				assertEquals(expectedForOthers, clients[i].readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect an error message to be sent when a user is added to a conversation by a user
	 * who is not connected to the server.
	 */
	@Test(timeout=10000) public void notConnectedAddToConvTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 1; i < clients.length; ++i) { // don't connect user0
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			clients[0] = new TestClient(NetworkConstants.DEFAULT_PORT);
			waitForNetworkMessage();
			clients[2].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 3; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv"); // user0, user1 not in conv
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					clients[i].readLine();
			}
			clients[0].send(NetworkConstants.ADD_TO_CONV + "\tuser1\tconv"); // add user1 to conv
			String expected = NetworkConstants.ERROR + "\t" + NetworkConstants.ADD_TO_CONV + "\tuser1\tconv";
			assertEquals(expected, clients[0].readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}

	/**
	 * Expect an entered conversation message to be sent when a user who is not in a given
	 * conversation sends an enter conversation message requesting to join it, when
	 * the conversation is previously empty.
	 */
	@Test(timeout=10000) public void notInConvEmptyEnterConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser1");
			client2.send(NetworkConstants.CONNECT + "\tuser2");
			waitForNetworkMessage();
			client1.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client1.send(NetworkConstants.EXIT_CONV + "\tconv");
			waitForNetworkMessage();
			client2.send(NetworkConstants.ENTER_CONV + "\tconv");
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ENTERED_CONV + "\tconv\tuser2";
			while(client2.ready())
				line = client2.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}
	
	/**
	 * Expect an entered conversation message to be sent when a user who is not in a given
	 * conversation sends an enter conversation message requesting to join it, and that an
	 * added to conversation message is sent to everyone else in the conversation, when
	 * the conversation is previously nonempty.
	 */
	@Test(timeout=10000) public void notInConvNonEmptyEnterConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser1");
			client2.send(NetworkConstants.CONNECT + "\tuser2");
			waitForNetworkMessage();
			client1.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client2.send(NetworkConstants.ENTER_CONV + "\tconv");
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ENTERED_CONV + "\tconv\t" +
					NetworkConstants.USERNAME + "\t" + NetworkConstants.USERNAME;
			while(client2.ready())
				line = client2.readLine();
			assertTrue(line.matches(expected));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}

	/**
	 * Expect an entered conversation message to be sent when a user who is already in a given
	 * conversation sends an enter conversation message requesting to join it.
	 */
	@Test(timeout=2000) public void inConvEnterConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client.send(NetworkConstants.ENTER_CONV + "\tconv");
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ENTERED_CONV + "\tconv\tuser";
			while(client.ready())
				line = client.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends an enter conversation message.
	 */
	@Test(timeout=2000) public void notConnectedEnterConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser1"); // connect user1 and not user2
			waitForNetworkMessage();
			client1.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client1.send(NetworkConstants.EXIT_CONV + "\tconv");
			waitForNetworkMessage();
			client2.send(NetworkConstants.ENTER_CONV + "\tconv");
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.ENTER_CONV + "\tconv";
			while(client2.ready())
				line = client2.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}

	/**
	 * Expect an error message to be sent when a user tries to enter a conversation that
	 * does not exist.
	 */
	@Test(timeout=2000) public void convDoesNotExistEnterConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.ENTER_CONV + "\tconv"); // does not exist
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.ENTER_CONV + "\tconv";
			while(client.ready())
				line = client.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect an error message to be sent when a user who is not in a given
	 * conversation sends an exit conversation message requesting to leave it.
	 */
	@Test(timeout=2000) public void notInConvExitConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser1");
			client2.send(NetworkConstants.CONNECT + "\tuser2");
			waitForNetworkMessage();
			client1.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client2.send(NetworkConstants.EXIT_CONV + "\tconv"); // user2 not in conv
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.EXIT_CONV + "\tconv";
			while(client2.ready())
				line = client2.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}

	/**
	 * Expect a removed from conversation message to be sent to all other users in the conversation 
	 * when a user who is in a given conversation sends an exit conversation message
	 * requesting to leave it.
	 */
	@Test(timeout=2000) public void inConvExitConvTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) {
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 1; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv"); // All clients in conv
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					clients[i].readLine();
			}
			clients[0].send(NetworkConstants.EXIT_CONV + "\tconv"); // user0 leave conv
			String expected = NetworkConstants.REMOVED_FROM_CONV + "\tuser0\tconv";
			for(int i = 1; i < clients.length; ++i)
				assertEquals(expected, clients[i].readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends an exit conversation message for an existing conversation.
	 */
	@Test(timeout=2000) public void notConnectedExitConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser1"); // don't connect user2
			waitForNetworkMessage();
			client1.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client2.send(NetworkConstants.EXIT_CONV + "\tconv"); // user2 not connected
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.EXIT_CONV + "\tconv";
			while(client2.ready())
				line = client2.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}
	
	/**
	 * Expect an error message to be sent when a user tries to exit a conversation that
	 * does not exist.
	 */
	@Test(timeout=2000) public void convDoesNotExistExitConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.EXIT_CONV + "\tconv"); // conv doesn't exist
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.EXIT_CONV + "\tconv";
			while(client.ready())
				line = client.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends a disconnect message.
	 */
	@Test(timeout=2000) public void notConnectedDisconnectTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.DISCONNECT);
			waitForNetworkMessage();
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.DISCONNECT;
			line = client.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}

	/**
	 * Expect a disconnected message to be sent to all other connected users when a user who is
	 * connected to the server sends a disconnect message.
	 */
	@Test(timeout=10000) public void connectedDisconnectTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) { // connect everyone
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			for(int i = 0; i < clients.length; ++i) {
				while(clients[i].ready())
					clients[i].readLine();
			}
			clients[0].send(NetworkConstants.DISCONNECT); // disconnect user0
			waitForNetworkMessage();
			String expected = NetworkConstants.DISCONNECTED + "\tuser0";
			for(int i = 1; i < clients.length; ++i) {
				assertEquals(expected, clients[i].readLine());
			}
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}

	/**
	 * Expect a participants message to be sent when a user who is connected to the server
	 * sends a retrieve participants message to the server for a conversation that exists
	 * but is empty.
	 */
	@Test(timeout=5000) public void convExistsAndEmptyRetrieveParticipantsTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client.send(NetworkConstants.EXIT_CONV + "\tconv");
			waitForNetworkMessage();
			client.send(NetworkConstants.RETRIEVE_PARTICIPANTS + "\tconv");
			String line = null;
			String expected = NetworkConstants.PARTICIPANTS + "\tconv";
			waitForNetworkMessage();
			while(client.ready())
				line = client.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}

	/**
	 * Expect a participants message to be sent when a user who is connected to the server
	 * sends a retrieve participants message to the server for a conversation that exists
	 * and is non-empty.
	 */
	@Test(timeout=10000) public void convExistsAndNonEmptyRetrieveParticipantsTest() {
		IMServer server = null;
		TestClient[] clients = new TestClient[5];
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			for(int i = 0; i < clients.length; ++i) { 
				clients[i] = new TestClient(NetworkConstants.DEFAULT_PORT);
				clients[i].send(NetworkConstants.CONNECT + "\tuser" + i);
			}
			waitForNetworkMessage();
			clients[0].send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			for(int i = 1; i < clients.length; ++i)
				clients[i].send(NetworkConstants.ENTER_CONV + "\tconv"); // all in conv
			waitForNetworkMessage();
			while(clients[0].ready())
				clients[0].readLine();
			clients[0].send(NetworkConstants.RETRIEVE_PARTICIPANTS + "\tconv");
			StringBuilder expected = new StringBuilder(NetworkConstants.PARTICIPANTS + "\tconv");
			for(int i = 0; i < clients.length; ++i)
				expected.append("\t" + NetworkConstants.USERNAME);
			assertTrue(clients[0].readLine().matches(expected.toString()));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			for(TestClient client : clients) {
				if(client != null)
					client.close();
			}
		}
	}

	/**
	 * Expect an error message to be sent when a user who is connected to the server
	 * sends a retrieve participants message to the server for a conversation that does not
	 * exist.
	 */
	@Test(timeout=2000) public void convDoesNotExistRetrieveParticipantsTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client.send(NetworkConstants.RETRIEVE_PARTICIPANTS + "\tconv");
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.PARTICIPANTS + "\tconv";
			waitForNetworkMessage();
			while(client.ready())
				line = client.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends a retrieve participants message to the server.
	 */
	@Test(timeout=2000) public void notConnectedRetrieveParticipantsTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client1.send(NetworkConstants.NEW_CONV + "\tconv");
			waitForNetworkMessage();
			client2.send(NetworkConstants.RETRIEVE_PARTICIPANTS + "\tconv");
			String line = null;
			String expected = NetworkConstants.ERROR + "\t" +
					NetworkConstants.PARTICIPANTS + "\tconv";
			waitForNetworkMessage();
			while(client2.ready())
				line = client2.readLine();
			assertEquals(expected, line);
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}
	
	/**
	 * Expect entered conversation messages to be returned when a user sends a two way
	 * conversation message requesting a conversation with another connected user.
	 */
	@Test(timeout=2000) public void bothConnectedTwoWayConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser1");
			client2.send(NetworkConstants.CONNECT + "\tuser2");
			waitForNetworkMessage();
			client1.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			client1.readLine();
			String line1 = client1.readLine();
			if(line1.startsWith(NetworkConstants.CONNECTED)) // Received message that other user connected
				line1 = client1.readLine();
			client2.readLine();
			String line2 = client2.readLine();
			if(line2.startsWith(NetworkConstants.CONNECTED)) // Received message that other user connected
				line2 = client2.readLine();
			assertTrue(line1.matches(NetworkConstants.ENTERED_CONV + "\t" +
					NetworkConstants.CONV_NAME + "\t" + NetworkConstants.USERNAME + "\t" +
					NetworkConstants.USERNAME));
			assertTrue(line2.matches(NetworkConstants.ENTERED_CONV + "\t" +
					NetworkConstants.CONV_NAME + "\t" + NetworkConstants.USERNAME + "\t" +
					NetworkConstants.USERNAME));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, user sending the message is connected, but the username sent
	 * in the message is not connected.
	 */
	@Test(timeout=2000) public void user1ConnectedTwoWayConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.CONNECT + "\tuser1");
			client.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			client.readLine();
			assertTrue(client.readLine().matches(NetworkConstants.ERROR + "\t" +
					NetworkConstants.TWO_WAY_CONV + "\tuser2"));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, the user sending the message is not connected, but the username
	 * sent in the message is connected.
	 */
	@Test(timeout=2000) public void user2ConnectedTwoWayConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client2.send(NetworkConstants.CONNECT + "\tuser2");
			waitForNetworkMessage();
			client1.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			String line1 = client1.readLine();
			assertTrue(line1.matches(NetworkConstants.ERROR + "\t" +
					NetworkConstants.TWO_WAY_CONV + "\tuser2"));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, the user sending the message is not connected, and the username
	 * sent in the message is not connected.
	 */
	@Test(timeout=2000) public void neitherConnectedTwoWayConvTest() {
		IMServer server = null;
		TestClient client = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			assertTrue(client.readLine().matches(NetworkConstants.ERROR + "\t" +
					NetworkConstants.TWO_WAY_CONV + "\tuser2"));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client != null)
				client.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, both users are connected, and both users have the same name.
	 */
	@Test(timeout=2000) public void sameNameTwoWayConvTest() {
		IMServer server = null;
		TestClient client1 = null;
		TestClient client2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			client1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			client2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			client1.send(NetworkConstants.CONNECT + "\tuser");
			client2.send(NetworkConstants.CONNECT + "\tuser");
			waitForNetworkMessage();
			client1.send(NetworkConstants.TWO_WAY_CONV + "\tuser");
			client1.readLine();
			String line = client1.readLine();
			if(line.startsWith(NetworkConstants.CONNECTED)) // Received message that other user connected
				line = client1.readLine();
			assertTrue(line.matches(NetworkConstants.ERROR + "\t" +
					NetworkConstants.TWO_WAY_CONV + "\tuser"));
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(server != null) {
				try {
					server.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
			if(client1 != null)
				client1.close();
			if(client2 != null)
				client2.close();
		}
	}
	
}

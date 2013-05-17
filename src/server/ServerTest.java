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
	
	//TODO: IMPLEMENT THE TESTS THAT HAVE ONLY A METHOD HEADER.
	
	/**
	 * Ensure that correct message is returned from the server when one user
	 * logs in.
	 */
	@Test(timeout=1000) public void oneUserLoginTest() {
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
	@Test(timeout=1000) public void nameTakenLoginTest() {
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
			long time = System.currentTimeMillis(); // Wait to make sure other user is connected.
			while(System.currentTimeMillis() - time < 200);
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
	@Test(timeout=1000) public void multipleUserLoginTest() {
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
			long time = System.currentTimeMillis(); // Wait to make sure other users are connected.
			while(System.currentTimeMillis() - time < 200);
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
			long time = System.currentTimeMillis(); // Wait to make sure other users are connected.
			while(System.currentTimeMillis() - time < 200);
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
	@Test(timeout=1000) public void inConvMultipleUserIMTest() {
		
	}
	
	/**
	 * Expect IM message to be sent to the sender when the sender of an IM message
	 * is in the conversation but no other clients are in the conversation.
	 */
	@Test(timeout=1000) public void inConvOneUserIMTest() {
		
	}
	
	/**
	 * Expect error message to be sent to sending client when the sender of an IM message
	 * is not in the conversation but is connected to the server.
	 */
	@Test(timeout=1000) public void notInConvIMTest() {
		
	}
	
	/**
	 * Expect error message to be sent to sending client when the sender of an IM message
	 * is not in the conversation or connected to the server.
	 */
	@Test(timeout=1000) public void notConnectedMultipleUserIMTest() {
		
	}
	
	/**
	 * Expect error message to be sent to sending client when an IM message
	 * is sent to a conversation that doesn't exist.
	 */
	@Test(timeout=1000) public void convDoesNotExistIMTest() {
		
	}
	
	/**
	 * Expect enter conversation message to be sent to the sender of a new conversation message
	 * when the conversation name is unused and the sender is connected to the server.
	 */
	@Test(timeout=1000) public void unusedNameAndConnectedNewConvTest() {
		
	}
	
	/**
	 * Expect no message to be sent to the sender of a new conversation message
	 * when the conversation name is used.
	 */
	@Test(timeout=1000) public void usedNameNewConvTest() {
		
	}
	
	/**
	 * Expect no message to be sent to the sender of a new conversation message
	 * when the conversation name is unused and the sender is disconnected to the server.
	 */
	@Test(timeout=1000) public void unusedNameAndDisconnectedNewConvTest() {
		
	}
	
	/**
	 * Expect enter conversation message to be sent to a user and an added to conversation
	 * message to every other user when a user is added to a conversation by a user who is
	 * already in the conversation.
	 */
	@Test(timeout=1000) public void inConvAndConnectedAndUserExistsAndIsNotInConvAddToConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user is added to a conversation by a user who
	 * is already in the conversation if the user he adds is also already in the conversation.
	 */
	@Test(timeout=1000) public void inConvAndConnectedAndUserExistsAndIsInConvAddToConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a non-existent user is added to a conversation
	 * by a user who is already in the conversation.
	 */
	@Test(timeout=1000) public void inConvAndConnectedAndUserDoesNotExistAddToConvTest() {
		
	}

	/**
	 * Expect an error message to be sent when a user is added to a conversation by a user
	 * who is not already in the conversation but is connected to the server.
	 */
	@Test(timeout=1000) public void notInConvAndConnectedAddToConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user is added to a conversation by a user
	 * who is not connected to the server.
	 */
	@Test(timeout=1000) public void notConnectedAddToConvTest() {
		
	}

	/**
	 * Expect an entered conversation message to be sent when a user who is not in a given
	 * conversation sends an enter conversation message requesting to join it, when
	 * the conversation is previously empty.
	 */
	@Test(timeout=1000) public void notInConvEmptyEnterConvTest() {
		
	}
	
	/**
	 * Expect an entered conversation message to be sent when a user who is not in a given
	 * conversation sends an enter conversation message requesting to join it, and that an
	 * added to conversation message is sent to everyone else in the conversation, when
	 * the conversation is previously nonempty.
	 */
	@Test(timeout=1000) public void notInConvNonEmptyEnterConvTest() {
		
	}

	/**
	 * Expect an error conversation message to be sent when a user who is already in a given
	 * conversation sends an enter conversation message requesting to join it.
	 */
	@Test(timeout=1000) public void inConvEnterConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends an enter conversation message.
	 */
	@Test(timeout=1000) public void notConnectedEnterConvTest() {
		
	}

	/**
	 * Expect an error message to be sent when a user tries to enter a conversation that
	 * does not exist.
	 */
	@Test(timeout=1000) public void convDoesNotExistEnterConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user who is not in a given
	 * conversation sends an exit conversation message requesting to leave it.
	 */
	@Test(timeout=1000) public void notInConvExitConvTest() {
		
	}

	/**
	 * Expect an removed from conversation message to be sent when a user who is in a given
	 * conversation sends an exit conversation message requesting to leave it.
	 */
	@Test(timeout=1000) public void inConvExitConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends an exit conversation message.
	 */
	@Test(timeout=1000) public void notConnectedExitConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user tries to exit a conversation that
	 * does not exist.
	 */
	@Test(timeout=1000) public void convDoesNotExistExitConvTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends a disconnect message.
	 */
	@Test(timeout=1000) public void notConnectedDisconnectTest() {
		
	}

	/**
	 * Expect a disconnected message to be sent to all other connected users when a user who is
	 * connected to the server sends a disconnect message.
	 */
	@Test(timeout=1000) public void connectedDisconnectTest() {
		
	}

	/**
	 * Expect a participants message to be sent when a user who is connected to the server
	 * sends a retrieve participants message to the server for a conversation that exists
	 * but is empty.
	 */
	@Test(timeout=1000) public void convExistsAndEmptyRetrieveParticipantsTest() {
		
	}

	/**
	 * Expect a participants message to be sent when a user who is connected to the server
	 * sends a retrieve participants message to the server for a conversation that exists
	 * and is non-empty.
	 */
	@Test(timeout=1000) public void convExistsAndNonEmptyRetrieveParticipantsTest() {
		
	}

	/**
	 * Expect an error message to be sent when a user who is connected to the server
	 * sends a retrieve participants message to the server for a conversation that does not
	 * exist.
	 */
	@Test(timeout=1000) public void convDoesNotExistRetrieveParticipantsTest() {
		
	}
	
	/**
	 * Expect an error message to be sent when a user who is not connected to the server
	 * sends a retrieve participants message to the server.
	 */
	@Test(timeout=1000) public void notConnectedRetrieveParticipantsTest() {
		
	}
	
	/**
	 * Expect entered conversation messages to be returned when a user sends a two way
	 * conversation message requesting a conversation with another connected user.
	 */
	@Test(timeout=1000) public void bothConnectedTwoWayConvTest() {
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
			long time = System.currentTimeMillis(); // Wait to ensure connection.
			while(System.currentTimeMillis() - time < 200);
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
	@Test(timeout=1000) public void user1ConnectedTwoWayConvTest() {
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
	@Test(timeout=1000) public void user2ConnectedTwoWayConvTest() {
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
			long time = System.currentTimeMillis(); // Wait to ensure connection.
			while(System.currentTimeMillis() - time < 200);
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
	@Test(timeout=1000) public void neitherConnectedTwoWayConvTest() {
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
	@Test(timeout=1000) public void sameNameTwoWayConvTest() {
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
			long time = System.currentTimeMillis(); // Wait to ensure connection.
			while(System.currentTimeMillis() - time < 200);
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

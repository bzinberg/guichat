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

	/* TODO:
	 * IM
	 * New conv
	 * Add to conv
	 * Enter conv
	 * Exit conv
	 * Disconnect
	 * Retrieve Participants
	 */
	
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
	
	/**
	 * Expect IM message to be sent to the sender when the sender of an IM message
	 * is in the conversation but no other clients are in the conversation.
	 */
	
	/**
	 * Expect error message to be sent to sending client when the sender of an IM message
	 * is not in the conversation but is connected to the server.
	 */
	
	/**
	 * Expect error message to be sent to sending client when the sender of an IM message
	 * is not in the conversation or connected to the server.
	 */
	
	/**
	 * Expect error message to be sent to sending client when an IM message
	 * is sent to a conversation that doesn't exist.
	 */
	
	/**
	 * Expect enter conversation message to be sent to the sender of a new conversation message
	 * when the conversation name is unused and the sender is connected to the server.
	 */
	
	/**
	 * Expect error message to be sent to the sender of a new conversation message
	 * when the conversation name is unused and the sender is connected to the server.
	 */
	
	/**
	 * Expect entered conversation messages to be returned when a user sends a two way
	 * conversation message requesting a conversation with another connected user.
	 */
	@Test(timeout=1000) public void twoWayConvBothConnectedTest() {
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
	@Test(timeout=1000) public void twoWayConvUser1ConnectedTest() {
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
	@Test(timeout=1000) public void twoWayConvUser2ConnectedTest() {
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
	@Test(timeout=1000) public void twoWayConvNeitherConnectedTest() {
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
	@Test(timeout=1000) public void twoWayConvSameNameTest() {
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

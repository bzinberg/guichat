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
	 * Finish 0
	 * 1
	 * 2
	 * 3
	 * 4
	 * 5
	 * 6
	 * 7
	 */
	
	/**
	 * Ensure that correct message is returned from the server when one user
	 * logs in.
	 */
	@Test(timeout=1000) public void oneUserLoginTest() {
		IMServer server = null;
		TestClient util = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util.send(NetworkConstants.CONNECT + "\ta");
			assertEquals(NetworkConstants.INIT_USERS_LIST + "\ta", util.readLine());
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
			if(util != null)
				util.close();
		}
	}

	/**
	 * Expect entered conv messages to be returned when two way conv message is
	 * sent and both users are connected.
	 */
	@Test(timeout=1000) public void twoWayConvBothConnectedTest() {
		IMServer server = null;
		TestClient util1 = null;
		TestClient util2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			util2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util1.send(NetworkConstants.CONNECT + "\tuser1");
			util2.send(NetworkConstants.CONNECT + "\tuser2");
			long time = System.currentTimeMillis(); // Wait to ensure connection.
			while(System.currentTimeMillis() - time < 200);
			util1.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			util1.readLine();
			String line1 = util1.readLine();
			if(line1.startsWith(NetworkConstants.CONNECTED)) // Received message that other user connected
				line1 = util1.readLine();
			util2.readLine();
			String line2 = util2.readLine();
			if(line2.startsWith(NetworkConstants.CONNECTED)) // Received message that other user connected
				line2 = util2.readLine();
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
			if(util1 != null)
				util1.close();
			if(util2 != null)
				util2.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, user sending the message is connected, but the username sent
	 * in the message is not connected.
	 */
	@Test(timeout=1000) public void twoWayConvUser1ConnectedTest() {
		IMServer server = null;
		TestClient util = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util.send(NetworkConstants.CONNECT + "\tuser1");
			util.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			util.readLine();
			assertTrue(util.readLine().matches(NetworkConstants.ERROR + "\t" +
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
			if(util != null)
				util.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, the user sending the message is not connected, but the username
	 * sent in the message is connected.
	 */
	@Test(timeout=1000) public void twoWayConvUser2ConnectedTest() {
		IMServer server = null;
		TestClient util1 = null;
		TestClient util2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			util2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util2.send(NetworkConstants.CONNECT + "\tuser2");
			long time = System.currentTimeMillis(); // Wait to ensure connection.
			while(System.currentTimeMillis() - time < 200);
			util1.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			String line1 = util1.readLine();
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
			if(util1 != null)
				util1.close();
			if(util2 != null)
				util2.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, the user sending the message is not connected, and the username
	 * sent in the message is not connected.
	 */
	@Test(timeout=1000) public void twoWayConvNeitherConnectedTest() {
		IMServer server = null;
		TestClient util = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util.send(NetworkConstants.TWO_WAY_CONV + "\tuser2");
			assertTrue(util.readLine().matches(NetworkConstants.ERROR + "\t" +
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
			if(util != null)
				util.close();
		}
	}
	
	/**
	 * Expect error message to be returned when two way conv message is
	 * sent, both users are connected, and both users have the same name.
	 */
	@Test(timeout=1000) public void twoWayConvSameNameTest() {
		IMServer server = null;
		TestClient util1 = null;
		TestClient util2 = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util1 = new TestClient(NetworkConstants.DEFAULT_PORT);
			util2 = new TestClient(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util1.send(NetworkConstants.CONNECT + "\tuser");
			util2.send(NetworkConstants.CONNECT + "\tuser");
			long time = System.currentTimeMillis(); // Wait to ensure connection.
			while(System.currentTimeMillis() - time < 200);
			util1.send(NetworkConstants.TWO_WAY_CONV + "\tuser");
			util1.readLine();
			String line = util1.readLine();
			if(line.startsWith(NetworkConstants.CONNECTED)) // Received message that other user connected
				line = util1.readLine();
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
			if(util1 != null)
				util1.close();
			if(util2 != null)
				util2.close();
		}
	}
	
}

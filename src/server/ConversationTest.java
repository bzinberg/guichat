package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import network.NetworkConstants;

import org.junit.Test;

import test.TestClient;

/**
 * 	JUnit Tests for the Conversation class.
 * 
 *  @category no_didit
 */
public class ConversationTest {
	
	/* TODO:
	 * Two user constructor/toString test
	 * add tests
	 * remove tests
	 * contains tests
	 * sendMessage test(s) - might only be one case
	 * toArray tests (no users, some users)
	 * getName test
	 */
	
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
		u.setUsername("user");
		Conversation conv = new Conversation("conv", u);
		assertEquals("conv\tuser", conv.toString());
		u.interrupt();
	}
	
	/**
	 * Check that constructor with one initial user and toString work.
	 */
	@Test
	public void twoUserConstructorTest() {
		ServerSocket serverSocket = null;
		Socket socket;
		User u = null;
		User v = null;
		try {
			serverSocket = new ServerSocket(NetworkConstants.DEFAULT_PORT);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			u = new User(null, socket);
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
			serverSocket.accept();
			v = new User(null, socket);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		}
		u.setUsername("user1");
		v.setUsername("user2");
		Conversation conv = new Conversation("conv", u, v);
		assertEquals("conv\tuser2\tuser1", conv.toString());
		u.interrupt();
		v.interrupt();
		try {
			serverSocket.close();
		} catch (IOException e) {
			assertTrue(false);
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

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
	@Test
	public void oneUserConstructorTest() {
		ServerSocket serverSocket;
		Socket socket;
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
		}
		u.setUsername("user");
		Conversation conv = new Conversation("conv", u);
		assertEquals("conv\tuser", conv.toString());
	}

	/**
	 * Expect that hashCode returns the Conversation's name's hashCode.
	 */
	@Test(timeout=1000) public void hashcodeTest() {
		Conversation conv = new Conversation("conv");
		assertEquals("conv".hashCode(), conv.hashCode());
	}

}

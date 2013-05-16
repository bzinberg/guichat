package server;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import network.NetworkConstants;

import org.junit.Test;

/**
 * 
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
	@Test
	public void oneUserConstructorTest() {
		Socket socket;
		User u = null;
		try {
			socket = new Socket("localhost", NetworkConstants.DEFAULT_PORT);
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

}

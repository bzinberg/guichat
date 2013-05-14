package server;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import test.TestUtility;
import network.NetworkConstants;

/**
 * JUnit tests for the server. Sends messages to the server and checks for the
 * correct responses.
 * 
 * @category no_didit
 */
public class ServerTest {

	/**
	 * Ensure that correct message is returned from the server when one user
	 * logs in.
	 */
	@Test public void oneUserLoginTest() {
		IMServer server;
		TestUtility util = null;
		Thread serverThread = null;
		try {
			server = new IMServer(NetworkConstants.DEFAULT_PORT);
			util = new TestUtility(NetworkConstants.DEFAULT_PORT);
			serverThread = new Thread(server);
			serverThread.start();
			util.send("0\ta");
			assertEquals("0\ta", util.readLine());
		} catch(IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} finally {
			if(serverThread != null)
				serverThread.interrupt();
			if(util != null)
				util.close();
		}
	}
	
	
}

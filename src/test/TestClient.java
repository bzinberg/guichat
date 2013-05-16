package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import network.NetworkConstants;

/**
 * TestClient is mock version of the client used to test different parts of the server.
 * TestClient provides an automated way to send messages to and read responses from
 * the server.
 */
public class TestClient {
	
	private Socket socket;
	private PrintWriter out;
 	private BufferedReader in;
	
 	/**
 	 * Creates a new instance of TestUtility to connect to localhost at the
 	 * specified port.
 	 * 
 	 * @param port The port on which to get a connection.
 	 * @throws UnknownHostException
 	 * @throws IOException
 	 */
	public TestClient(int port) throws UnknownHostException, IOException {
		socket = new Socket("localhost", port);
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/**
	 * Creates an instance of TestUtility on the default port, specified in
	 * network.NetworkConstants.
	 * 
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public TestClient() throws UnknownHostException, IOException {
		this(NetworkConstants.DEFAULT_PORT);
	}

	/**
	 * Closes this TestUtility by closing each of this.in, this.out, and
	 * this.socket.  Should be called whenever communication is completed.
	 */
	public void close() {
		try { in.close(); }
		catch(IOException ee) {}
		try { socket.close(); }
		catch(IOException ee) {}
		out.close();
	}

	/**
	 * Read the next line in the socket, sent from the server or client.
	 * Blocks until a newline is printed in the socket.
	 * 
	 * @return The next line received from the server of client.
	 * @throws IOException
	 */
	public String readLine() throws IOException {
		return in.readLine();
	}

	/**
	 * Sends the given String to the server.
	 * 
	 * @param s The String to send to the server.
	 */
	public void send(String s) {
		out.println(s);
		out.flush();
	}
	
	/**
	 * Tells whether the TestClient's input stream is ready to be read.
	 * 
	 * @return True if the server has sent anything to the socket; false otherwise.
	 * @throws IOException
	 */
	public boolean ready() throws IOException {
		return in.ready();
	}

}
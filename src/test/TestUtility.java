package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import network.NetworkConstants;


public class TestUtility {
	
	private Socket connection;
	private PrintWriter out;
 	private BufferedReader in;
	
	public TestUtility(int portNumber) throws UnknownHostException, IOException {
		connection = new Socket("localhost", portNumber);
		out = new PrintWriter(connection.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}

	public TestUtility() throws UnknownHostException, IOException {
		this(NetworkConstants.DEFAULT_PORT);
	}

	public void close() {
		try { in.close(); }
		catch(IOException ee) {}
		try { connection.close(); }
		catch(IOException ee) {}
		out.close();
	}

	public String readLine() throws IOException {
		return in.readLine();
	}

	public void send(String s) {
		out.println(s);
		out.flush();
	}

}
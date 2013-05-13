package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import main.NetworkConstants;

public class TestUtility extends Thread {
	
	private Socket connection;
	private PrintWriter out;
 	private BufferedReader in;
 	private List<String> lines;
	
	public TestUtility(int portNumber) throws UnknownHostException, IOException {
		connection = new Socket("localhost", portNumber);
		out = new PrintWriter(connection.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		lines = new ArrayList<String>();
	}
	
	public TestUtility() throws UnknownHostException, IOException {
		this(NetworkConstants.DEFAULT_PORT);
	}
	
	@Override
	public void run() {
		try {
			String line;
			for (line = in.readLine(); line != null; line = in.readLine())
				synchronized(lines) {
					lines.add(line);
				}
		}
		catch(Exception e) {
		}
		finally {
			try { in.close(); }
			catch(IOException ee) {}
			try { connection.close(); }
			catch(IOException ee) {}
			out.close();
		}
	}
	
	public int getNumLines() {
		synchronized(lines) {
			return lines.size();
		}
	}
	
	public String getLine(int n) {
		synchronized(lines) {
			return lines.get(n);
		}
	}
	
	public String getLastLine() {
		synchronized(lines) {
			if (lines.size() == 0) return null;
			return lines.get(lines.size()-1);
		}
	}
	
	public String getAllLines() {
		StringBuilder ret = new StringBuilder();
		for (String s : lines)
			ret.append(s); ret.append("\n");
		return ret.toString();
	}
	
	public void send(String s) {
		synchronized(out) {
			out.println(s);
			out.flush();
		}
	}
	
}
/**
 * 
 */
package replicatorg.drivers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.w3c.dom.Node;

import replicatorg.app.Base;
import replicatorg.app.tools.XML;

/**
 * @author d1plo1d
 *
 */
public class NetworkDriver extends DriverBaseImplementation implements ConnectableDriver {

	protected Socket socket = null;
    protected PrintWriter out = null;
    protected BufferedReader in = null;
    
    private int port;
    private String address;

    private boolean explicit = false;
    
    protected NetworkDriver() {
    	address = Base.preferences.get("network.address",null);
    	port = Base.preferences.getInt("network.port", -1);
    }
    
	public void loadXML(Node xml) {
		super.loadXML(xml);
        // load from our XML config, if we have it.
        if (XML.hasChildNode(xml, "address")) {
            address = XML.getChildNodeValue(xml, "address");
            explicit = true;
        }
        if (XML.hasChildNode(xml, "port")) {
        		port = Integer.parseInt(XML.getChildNodeValue(xml, "port"));
        }
    	if (address == null || port < 0) { 
    		//TODO: what kinda exception should I through here to not crash this party?
    		throw new RuntimeException("the network driver requires a specified machine address and port");
    	}
	}

	public void setSocket(Socket socket) throws IOException {
		if (this.socket == socket) return;
		disposeSocket();
		setInitialized(false);
		this.socket = socket;
		// new input buffer + stream
		resetInput();
		// new output stream
		disposeOutput();
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public Socket getSocket() { return socket; }
	
	public String getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}
		
	public boolean isExplicit() { return explicit; }
	
	public void dispose() {
		super.dispose();
		disposeSocket();
		socket = null;
	}
	
	/**
	 * Resets the in buffered reader by replacing it with a fresh one.
	 * This can also be used to setup a new buffered reader for in. 
	 * @throws IOException if the new input stream fails to instantiate.
	 */
	public void resetInput() throws IOException {
		disposeInput();
		in = new BufferedReader(new InputStreamReader(
		                                socket.getInputStream()));
	}

	@Override
	public void connect() throws ConnectionException {
		try {
			Base.logger.info("Connecting to "+address+":"+port);
			setSocket(new Socket(address, port));
			Base.logger.info("Connected to "+address+":"+port);
		} catch (UnknownHostException e) {
			throw new ConnectionException("unknown host "+address+":"+port,e);
		} catch (IOException e) {
			throw new ConnectionException("could not connect to "+address+":"+port,e);
		}
	}

	/**
	 * Disposes of the current input. Make sure to set initialized to false 
	 * after this if you want to continue using this object.
	 */
	private void disposeInput()
	{
		if (in!=null)
		{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			in = null;
		}
	}

	/**
	 * Disposes of the current input. Make sure to set initialized to false 
	 * after this if you want to continue using this object.
	 */
	private void disposeOutput()
	{
		if (out!=null)
		{
			out.close();
			out = null;
		}
	}

	/**
	 * Disposes of the current socket. Make sure to set initialized to false 
	 * after this if you want to continue using this object.
	 */
	private void disposeSocket() {
		if (this.socket != null) {
			try {
				this.socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.socket = null;
		}
	}
}

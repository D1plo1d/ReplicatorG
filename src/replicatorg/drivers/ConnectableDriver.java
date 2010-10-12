/**
 * 
 */
package replicatorg.drivers;

/**
 * @author rob
 *
 */
public interface ConnectableDriver {
	
	/**
	 * Connects to the machine based on it's driver's parameters.
	 * @throws ConnectionException if the machine fails to connect.
	 */
	public void connect() throws ConnectionException;
	
	public class ConnectionException extends Exception
	{
		/**
		 * Compiler generated serialVersionUID
		 */
		private static final long serialVersionUID = -4096317393559466834L;

		public ConnectionException(Exception e)
		{
			super(e);
		}
		public ConnectionException(String message, Exception e)
		{
			super(message,e);
		}
		public ConnectionException(String message)
		{
			super(message);
		}
	}
}

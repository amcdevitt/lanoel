package computer.lanoel.exceptions;

public class InvalidSessionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String _sessionId;
	
	public InvalidSessionException(String message, String sessionId)
	{
		super(message);
		_sessionId = sessionId; 
		System.out.println("InvalidSessionException: " + message);
	}
	
	public InvalidSessionException(String message, Exception ex, String sessionId)
	{
		super(message, ex);
		_sessionId = sessionId;
		System.out.println("InvalidSessionException: " + message + "\n" + ex.getMessage());
	}
	
	public String getSessionId()
	{
		return _sessionId;
	}

}

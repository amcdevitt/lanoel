package computer.lanoel.communication;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;

public class HttpHelper {
	
	public static final String SESSION_HEADER = "sessionid";
	public static HttpHeaders commonHttpHeaders()
	{
		HttpHeaders headers = new HttpHeaders();
		return headers;
	}
	
	public static HttpHeaders commonHttpHeaders(String sessionId)
	{
		String updatedSession = SessionCache.getInstance().getCurrentSession(sessionId);
		HttpHeaders headers = commonHttpHeaders();
		if(updatedSession != null)
		{
			headers.add("sessionid", updatedSession);
		}
		return headers;
	}
	
	public static User getUserFromRequest(HttpServletRequest request)
	{
		String ipAddr = request.getRemoteAddr();
		String sessionId = request.getHeader(SESSION_HEADER);
		
		//Put the session in the cache
		SessionCache.getInstance().putNewSession(sessionId, sessionId);
		
		return new User(ipAddr, sessionId);		
	}
}

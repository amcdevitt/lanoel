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
		HttpHeaders headers = commonHttpHeaders();
		if(sessionId != null)
		{
			headers.add(SESSION_HEADER, sessionId);
		}
		return headers;
	}
	
	public static String getSessionIdFromHeaders(HttpHeaders requestHeaders)
	{
		return requestHeaders.getFirst(SESSION_HEADER);
	}
	
	public static User getUserFromRequest(HttpServletRequest request)
	{
		User user = new User();
		user.setSessionId(request.getHeader(SESSION_HEADER));
		return user;
	}
}

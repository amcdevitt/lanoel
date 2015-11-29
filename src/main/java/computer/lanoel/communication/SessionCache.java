package computer.lanoel.communication;

import java.util.HashMap;
import java.util.Map;

public class SessionCache
{
	private Map<String, String> _session;
	private static SessionCache cache = null;
	
	private SessionCache()
	{
		_session = new HashMap<String, String>();
	}
	
	public static SessionCache getInstance()
	{
		if(cache == null)
		{
			cache = new SessionCache();
		}
		return cache;
	}
	
	public void putNewSession(String oldSessionId, String newSessionId)
	{
		if(oldSessionId == null) return;
		_session.put(oldSessionId, newSessionId);
	}
	
	/**
	 * Returns the session if it is in the cache and then removes it from the cache.
	 * Returns null if the session is not in the cache
	 * @param sessionid
	 * @return
	 */
	public String getCurrentSession(String sessionid)
	{
		String sessionToReturn = _session.containsKey(sessionid) ? _session.get(sessionid) : null;
		
		if(sessionToReturn != null)
		{
			_session.remove(sessionid);
		}
		
		return sessionToReturn;
	}

}

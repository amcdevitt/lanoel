package computer.lanoel.communication;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.Order;

import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.Helper;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.platform.ServiceUtils;

public class Authorization
{
	private static UserAccountManagerClient _accountManager = new UserAccountManagerClient(ServiceConstants.accountServiceBaseUrl);
	
	Authorization()
	{

	}

	public static boolean userHasAccess(User user)
	{
 		if(user == null)
		{
			return false;
		}
 		
 		List<String> allowedUsers = new ArrayList<String>();
 		allowedUsers.add("Aaron");
 		allowedUsers.add("Bryon");
 		allowedUsers.add("bingemi");
 		allowedUsers.add("amcdevitt");
		
		try
		{
			if(allowedUsers.contains(user.getUserName())) return true;
			
		} catch (Exception e)
		{
			return false;
		}
		
		return false;
	}
	
	public static UserAccount validateUser(User user) throws InvalidSessionException
	{
		UserAccount uAcct = null;
		try
		{
			String currentUserSession = user.getSessionId();
			if(currentUserSession != null)
			{
				SessionCache.getInstance().putNewSession(currentUserSession, currentUserSession);
			}
			uAcct = _accountManager.getUserAccount(user.getSessionId());
			if(uAcct == null)
			{
				throw new InvalidSessionException("User not logged in!", user.getSessionId());
			}
			
			//We could have a new session because we are logged in
			SessionCache.getInstance().putNewSession(currentUserSession, uAcct.getUser().getSessionId());
			
			user.setUserName(uAcct.getUserName());
			
			// User is logged in at this point			
		}
		catch (Exception e)
		{
			throw new InvalidSessionException("User not logged in", user.getSessionId());
		}
		
		return uAcct;
	}
}

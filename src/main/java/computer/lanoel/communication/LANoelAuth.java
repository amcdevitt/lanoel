package computer.lanoel.communication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.core.annotation.Order;

import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.Helper;
import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.platform.ServiceUtils;

public class LANoelAuth
{
	private static UserAccountManagerClient _accountManager = new UserAccountManagerClient(ServiceConstants.accountServiceBaseUrl);
	
	private static final List<String> ADMIN_USERS = Arrays.asList("amcdevitt@gmail.com","bingemi@gmail.com");
	
	LANoelAuth()
	{

	}

	public static boolean isAdminUser(UserAccount user)
	{
		try
		{
			return ADMIN_USERS.contains(user.getUserName());
			
		} catch (Exception e)
		{
			return false;
		}
	}
	
	public static UserAccount loggedInUser(String sessionid) throws Exception
	{
		try
		{
			return _accountManager.getUserAccount(sessionid);
		} catch (Exception e)
		{
			throw new Exception("User Not logged in: " + e.getMessage());
		}
	}
}

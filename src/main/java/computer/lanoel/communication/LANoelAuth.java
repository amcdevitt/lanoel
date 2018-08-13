package computer.lanoel.communication;

import com.omegasixcloud.contracts.accounts.UserAccount;
import computer.lanoel.platform.ServiceConstants;

import java.util.Arrays;
import java.util.List;

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
			return ADMIN_USERS.contains(user.getUsername());
			
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

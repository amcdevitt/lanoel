package computer.lanoel.platform;

import java.util.Arrays;
import java.util.List;

public final class ServiceConstants {

	public static String getServiceNameAsciiImage()
	{
		return "LANOEL!!!!!!!!!!!!!!";
	}
	
	public static final String accountServiceBaseUrl = "https://accounts.svc.omegasixcloud.net/";
	
	public static final String steamAPIkey = "5EB620EC68DC1C21214D9DCEF7C90D89";

	public static final List<String> sqlUpgradePaths = Arrays.asList(
			"/code/resources/database/mysql",
			"database/mysql"
	);
}

package computer.lanoel.platform;

import java.util.Arrays;
import java.util.List;

public final class ServiceConstants {

	public static String getServiceNameAsciiImage()
	{
		return "LANOEL!!!!!!!!!!!!!!";
	}
	
	public static final String accountServiceBaseUrl = "https://accounts.omegasixcloud.net/";
	
	public static final String steamAPIkey = "9A42F07D3DA22AA10B81E1B00E93C20A";

	public static final List<String> sqlUpgradePaths = Arrays.asList(
			"/code/resources/database/mysql",
			"database/mysql"
	);
}

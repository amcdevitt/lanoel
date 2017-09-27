/**
 * 
 */
package computer.lanoel.platform;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
/**
 * @author amcdevitt
 *
 */
public class ServiceUtils {

    public static java.util.Date getCurrentTime()
    {
    	//TODO: Probably should be normalized to GMT
    	Calendar calendar = Calendar.getInstance();
    	calendar.setTimeZone(TimeZone.getTimeZone("est"));
        return calendar.getTime();
    }
    
    public static java.sql.Timestamp getCurrentTimeForSql()
    {
    	return new java.sql.Timestamp(getCurrentTime().getTime());
    }
	
	public static Map<String, String> getDatabaseProperties()
	{
		Map<String, String> dbProperties = new HashMap<String, String>();
		dbProperties.put("url", getEnvironmentVariables().get("lanoel_db_url"));
		dbProperties.put("username", getEnvironmentVariables().get("lanoel_db_username"));
		dbProperties.put("password", getEnvironmentVariables().get("lanoel_db_password"));
		
		return dbProperties;
	}
	
	private static Map<String, String> getEnvironmentVariables()
	{
		return System.getenv();
	}

	public static Connection getSingleDBConnection() throws SQLException
	{
		Map<String, String> connInfo = ServiceUtils.getDatabaseProperties();
		DriverManager.setLoginTimeout(5);
		Connection conn = DriverManager.getConnection(
				connInfo.get("url"), connInfo.get("username"), connInfo.get("password"));
		conn.setAutoCommit(false);

		return conn;
	}
}
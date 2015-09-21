/**
 * 
 */
package computer.lanoel.platform;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
/**
 * @author amcdevitt
 *
 */
public class ServiceUtils {

	private static DatabaseManager _storage;
	
	public static void setStorage(DatabaseManager storage)
	{
		_storage = storage;
	}
	
	public static DatabaseManager storage()
	{
		return _storage;
	}
    
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
		Properties prop = new Properties();
		Map<String, String> dbProperties = new HashMap<String, String>();

		try(InputStream input = new FileInputStream("dbproperties/database.properties")) {

			// load a properties file
			prop.load(input);

			// get the property value and print it out
			dbProperties.put("url", prop.getProperty("url"));
			dbProperties.put("username", prop.getProperty("username"));
			dbProperties.put("password", prop.getProperty("password"));

		} catch (IOException ex) {
			return null;
		}
		
		return dbProperties;
	}
}
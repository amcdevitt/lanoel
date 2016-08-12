package computer.lanoel.platform.database;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class DatabaseManager implements IDatabase{
	
	protected Connection conn;
	
	public DatabaseManager()
	{
	}
	
	public DatabaseManager(Connection connection)
	{
		conn = connection;
	}
	
	public boolean storageAvailable()
	{
		try {
			if(!conn.isValid(5))
			{
				conn.close();
				return false;
			}
			conn.close();
		} catch (SQLException e) {
			System.out.println("Ping DB failed: " + e.getMessage());
			return false;
		}
		
		return true;
	}
	
	public void UpgradeStorage() throws Exception
	{
		String currentSql = null;
        try {
        	URL url = Resources.getResource("database/mysql/createDatabase.sql");
    		String res = Resources.toString(url, Charsets.UTF_8);
    		System.out.println("Sql to run:\n" + res);
    		String[] sqls = res.split(";");
    		
    		for(String sql : sqls)
    		{
    			currentSql = sql;
    			PreparedStatement ps = conn.prepareStatement(sql);
        		ps.executeUpdate();
    		}
        	
        	conn.commit();

        } catch (SQLException ex) {
        	System.out.println("Failed on :\n" + currentSql);
        	System.out.println("error caught: " + ex.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
            	System.out.println("error caught: " + ex.getMessage());
            }
        }
	}
	
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}
}

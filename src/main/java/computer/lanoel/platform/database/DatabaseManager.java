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
	
	public void setConnection(Connection conn)
	{
		this.conn = conn;
	}

	public void commitAndClose() throws SQLException
	{
		this.conn.commit();
		this.conn.close();
	}
}

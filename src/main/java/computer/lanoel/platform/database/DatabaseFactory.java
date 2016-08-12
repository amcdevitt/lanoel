package computer.lanoel.platform.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;

import computer.lanoel.platform.ServiceUtils;

public class DatabaseFactory {

	private static DatabaseFactory instance;
	
	private DatabaseFactory()
	{
		
	}
	
	public static DatabaseFactory getInstance()
	{
		if(instance == null)
		{
			instance = new DatabaseFactory();
		}
		return instance;
	}
	
	private Connection getDBConnection() throws SQLException
	{
		Map<String, String> connInfo = ServiceUtils.getDatabaseProperties();
		DriverManager.setLoginTimeout(5);
		Connection conn = DriverManager.getConnection(
				connInfo.get("url"), connInfo.get("username"), connInfo.get("password"));
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	public IDatabase getDatabase(String dbType) throws Exception
	{
		return getDatabase(dbType, getDBConnection());
	}
	
	public IDatabase getDatabase(String dbType, Connection conn) throws Exception
	{
		IDatabase db = null;
		switch(dbType)
		{
		case "PERSON":
			db = new PersonDatabase(conn);
			break;
		case "GAME":
			db = new GameDatabase(conn);
			break;
		case "TOURNAMENT":
			db = new TournamentDatabase(conn);
			break;
		case "VOTE":
			db = new VoteDatabase(conn);
			break;
		default:
			db = new DatabaseManager(conn);
		}
		
		return db;
	}
}

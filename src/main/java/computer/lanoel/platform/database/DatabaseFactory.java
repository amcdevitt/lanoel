package computer.lanoel.platform.database;

import java.sql.Connection;

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
	
	public IDatabase getDatabase(String dbType) throws Exception
	{
		return getDatabase(dbType, ServiceUtils.getDBConnection());
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
			db = new TournamentLanoelDatabase(conn);
			break;
		case "VOTE":
			db = new VoteDatabase(conn);
			break;
		case "TOURNAMENT_SWISS":
			db = new TournamentSwissDatabase(conn);
			break;

		default:
			db = new DatabaseManager(conn);
		}
		
		return db;
	}
}

package computer.lanoel.platform.database.sql;

public class TournamentLanoelSql
{
	public static String tournamentInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO TournamentLanoel (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("TournamentName");
		valuesSql.append("?");
		insertSql.append("Type");
		valuesSql.append("lanoel");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	public static String tournamentUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE TournamentLanoel SET ");
		
		updateSql.append("TournamentName=?");
		
		whereSql.append(" WHERE TournamentKey=?;");
		return updateSql.append(whereSql).toString();
	}
	
	public static String roundInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO TournamentLanoel_Round (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("TournamentKey,");
		valuesSql.append("?,");
		insertSql.append("RoundNumber,");
		valuesSql.append("?,");
		insertSql.append("GameKey");
		valuesSql.append("?");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	public static String roundUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE TournamentLanoel_Round SET ");
		
		updateSql.append("GameKey=?");
		
		whereSql.append(" WHERE TournamentKey=? AND RoundNumber=?;");
		return updateSql.append(whereSql).toString();
	}
	
	public static String roundStandingInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO TournamentLanoel_RoundStanding (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("RoundKey,");
		valuesSql.append("?,");
		insertSql.append("PersonKey,");
		valuesSql.append("?,");
		insertSql.append("Place");
		valuesSql.append("?");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	public static String roundStandingUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE TournamentLanoel_RoundStanding SET ");
		
		updateSql.append("Place=?");
		
		whereSql.append(" WHERE RoundKey=? AND PersonKey=?;");
		return updateSql.append(whereSql).toString();
	}
}

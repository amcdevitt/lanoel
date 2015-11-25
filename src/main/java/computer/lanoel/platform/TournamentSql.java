package computer.lanoel.platform;

public class TournamentSql
{
	public static String tournamentInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO Tournament (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("TournamentName");
		valuesSql.append("?");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	public static String tournamentUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE Tournament SET ");
		
		updateSql.append("TournamentName=?");
		
		whereSql.append(" WHERE TournamentKey=?;");
		return updateSql.append(whereSql).toString();
	}
	
	public static String roundInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO Round (");
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
		updateSql.append("UPDATE Round SET ");
		
		updateSql.append("GameKey=?");
		
		whereSql.append(" WHERE TournamentKey=? AND RoundNumber=?;");
		return updateSql.append(whereSql).toString();
	}
	
	public static String roundStandingInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO RoundStanding (");
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
		updateSql.append("UPDATE RoundStanding SET ");
		
		updateSql.append("Place=?");
		
		whereSql.append(" WHERE RoundKey=? AND PersonKey=?;");
		return updateSql.append(whereSql).toString();
	}
}

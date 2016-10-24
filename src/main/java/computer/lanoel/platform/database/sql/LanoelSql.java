package computer.lanoel.platform.database.sql;

public class LanoelSql {

	public static final String UPDATE_PERSON = ""
			+ "UPDATE Person SET PersonName=?, Title=?, Information=?, UserName=? WHERE PersonKey=?;";
	
	public static final String UPDATE_GAME = ""
			+ "UPDATE Game SET GameName=?, Location=?, Rules=?, IsFree=? WHERE GameKey=?;";
	
	public static final String UPDATE_VOTE = ""
			+ "UPDATE Vote SET PersonKey=?, GameKey=?, VoteNumber=? WHERE VoteKey=?";
	
	public static final String INSERT_PERSON = ""
			+ "INSERT INTO Person (PersonName, Title, Information, UserName) VALUES (?,?,?,?);";

	public static final String INSERT_GAME = ""
			+ "INSERT INTO Game (GameName, Location, Rules, IsFree) VALUES (?,?,?,?);";

	public static final String INSERT_VOTE = ""
			+ "INSERT INTO Vote (PersonKey, GameKey, VoteNumber) VALUES (?,?,?);";
	
	public static final String INSERT_SUGGESTION = ""
			+ "INSERT INTO Suggestion (SuggestionKey, Description, Category) VALUES (?,?,?);";
	
	public static final String UPDATE_SUGGESTION = ""
			+ "UPDATE Suggestion SET Description=?, Category=? WHERE SuggestionKey=?;";
}

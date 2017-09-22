package computer.lanoel.platform.database.sql;

public class LanoelSql {

	public static final String UPDATE_PERSON = ""
			+ "UPDATE Person SET PersonData=? WHERE PersonKey=?;";
	
	public static final String UPDATE_GAME = ""
			+ "UPDATE Game SET GameData=? WHERE GameKey=?;";
	
	public static final String UPDATE_VOTE = ""
			+ "UPDATE Vote SET PersonKey=?, GameKey=?, VoteNumber=? WHERE VoteKey=?";
	
	public static final String INSERT_PERSON = ""
			+ "INSERT INTO Person (PersonData) VALUES (?);";

	public static final String INSERT_GAME = ""
			+ "INSERT INTO Game (GameData) VALUES (?);";

	public static final String INSERT_VOTE = ""
			+ "INSERT INTO Vote (PersonKey, GameKey, VoteNumber) VALUES (?,?,?);";
	
	public static final String INSERT_SUGGESTION = ""
			+ "INSERT INTO Suggestion (SuggestionKey, SuggestionData) VALUES (?,?,?);";
	
	public static final String UPDATE_SUGGESTION = ""
			+ "UPDATE Suggestion SET SuggestionData=? WHERE SuggestionKey=?;";
}

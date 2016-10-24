package computer.lanoel.platform.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Suggestion;
import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.database.sql.LanoelSql;

public class PersonDatabase extends DatabaseManager implements IDatabase {

	public PersonDatabase(Connection connection) {
		super(connection);
	}
	
	public Long insertPerson(Person person) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.INSERT_PERSON, Statement.RETURN_GENERATED_KEYS);
		
		int i = 1;
		ps.setString(i++, person.getPersonName());
		ps.setString(i++, person.getTitle());
		ps.setString(i++, person.getInformation());
		ps.setString(i++, person.getUserName());
		ps.executeUpdate();
		
		
		try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                person.setPersonKey(generatedKeys.getLong(1));
            }
            else {
                throw new SQLException("Item price insert failed, no ID obtained.");
            }
        }
		
		conn.commit();
		return person.getPersonKey();
	}
	
	public Long updatePerson(Person person) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.UPDATE_PERSON);
		
		int i = 1;
		ps.setString(i++, person.getPersonName());
		ps.setString(i++, person.getTitle());
		ps.setString(i++, person.getInformation());
		ps.setString(i++, person.getUserName());
		ps.setLong(i++, person.getPersonKey());
		ps.executeUpdate();
		
		conn.commit();
		return person.getPersonKey();
	}

	public Person getPerson(Long personKey) throws Exception
	{
		if(personKey == null) return null;
		
		VoteDatabase voteDb = (VoteDatabase) DatabaseFactory.getInstance().getDatabase("VOTE", conn);
		GameDatabase gameDb = (GameDatabase) DatabaseFactory.getInstance().getDatabase("GAME", conn);
		
		String selectSql = "SELECT * FROM Person WHERE PersonKey = ?";
		PreparedStatement ps = conn.prepareStatement(selectSql);
		ps.setLong(1, personKey);
		ResultSet rs = ps.executeQuery();

		if(!rs.isBeforeFirst()) return null; //No results
		
		Person personToReturn = new Person();
		while(rs.next())
		{
			personToReturn.setPersonKey(rs.getLong("PersonKey"));
			personToReturn.setPersonName(rs.getString("PersonName"));
			personToReturn.setTitle(rs.getString("Title"));
			personToReturn.setInformation(rs.getString("Information"));
			personToReturn.setUserName(rs.getString("UserName"));
		}
		
		List<Vote> votes = voteDb.getVotesForPerson(personToReturn.getPersonKey());
		if(votes == null) return personToReturn;
		for(Vote vote : votes)
		{
			if(vote.getVoteNumber() == 1)
			{
				personToReturn.setGameVote1(gameDb.getGame(vote.getGameKey()).getGameName());
			}
			
			if(vote.getVoteNumber() == 2)
			{
				personToReturn.setGameVote2(gameDb.getGame(vote.getGameKey()).getGameName());
			}
			
			if(vote.getVoteNumber() == 3)
			{
				personToReturn.setGameVote3(gameDb.getGame(vote.getGameKey()).getGameName());
			}
		}
		
		return personToReturn;
	}
	
	public List<Person> getPersonList() throws Exception
	{
		String selectSql = "SELECT * FROM Person;";
		PreparedStatement ps = conn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();

		if(!rs.isBeforeFirst()) new ArrayList<Person>();
		
		List<Person> personList = new ArrayList<Person>();
		while(rs.next())
		{
			personList.add(getPerson(rs.getLong("PersonKey")));				
		}
		return personList;
	}
	
	public Long getPersonKey(String personName) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement("SELECT PersonKey FROM Person WHERE PersonName like ?;");
		
		String tempName = null;
		try
		{
			tempName = personName.substring(personName.lastIndexOf(' '));
		} catch (Exception e)
		{
			//We might not have a space
			tempName = personName;
		}
		
		//if this didn't get set to anything, set it
		if(tempName == null)
		{
			tempName = personName;
		}
		
		ps.setString(1, "%" + tempName.trim());
		ResultSet rs = ps.executeQuery();
		
		if(!rs.isBeforeFirst()) return null;
		
		while(rs.next())
		{
			return rs.getLong("PersonKey");
		}		
		return null;
	}
	
	public void insertSuggestion(Suggestion sug) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.INSERT_SUGGESTION);
		
		int i = 1;
		ps.setString(i++, UUID.randomUUID().toString());
		ps.setString(i++, sug.Description);
		ps.setString(i++, sug.Category);
		ps.execute();
		conn.commit();
	}
	
	public Suggestion updateSuggestion(Suggestion sug) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.UPDATE_SUGGESTION);
		
		int i = 1;
		ps.setString(i++, sug.Description);
		ps.setString(i++, sug.Category);
		ps.setString(i++, sug.Key);
		
		ResultSet rs = ps.executeQuery();
		
		if(!rs.isBeforeFirst())
		{
			return null;
		}
		
		return sug;
	}
	
	public List<Suggestion> getSuggestions() throws Exception
	{
		List<Suggestion> sugList = new ArrayList<Suggestion>();
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM Suggestion;");
		
		ResultSet rs = ps.executeQuery();
		
		if(!rs.isBeforeFirst())
		{
			return sugList;
		}
		
		while(rs.next())
		{
			Suggestion sug = new Suggestion();
			sug.Key = rs.getString("SuggestionKey");
			sug.Description = rs.getString("Description");
			sug.Category = rs.getString("Category");
			sugList.add(sug);
		}
		
		return sugList;
	}
}

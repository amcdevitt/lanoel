package computer.lanoel.platform.database;

import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Suggestion;
import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.database.sql.LanoelSql;

public class PersonDatabase {

	private static Gson _gson;
	public PersonDatabase() {
		_gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
	}
	
	public Long insertPerson(Person person) throws Exception
	{
		QueryParameter qp = new QueryParameter(person, Types.OTHER);
		person.setPersonKey(DBConnection.queryWithParametersGetGeneratedKey(
				LanoelSql.INSERT_PERSON, Arrays.asList(qp)));

		return person.getPersonKey();
	}
	
	public Long updatePerson(Person person) throws Exception
	{
		QueryParameter qp1 = new QueryParameter(_gson.toJson(person), Types.OTHER);
		QueryParameter qp2 = new QueryParameter(person.getPersonKey(), Types.BIGINT);

		DBConnection.executeWithParams(LanoelSql.UPDATE_PERSON, Arrays.asList(qp1, qp2));

		return person.getPersonKey();
	}

	public Person getPerson(Long personKey) throws Exception
	{
		if(personKey == null) return null;

		String selectSql = "SELECT * FROM Person WHERE PersonKey = ?";

		QueryParameter qp1 = new QueryParameter(personKey, Types.BIGINT);

		List<Person> personList = DBConnection.queryWithParameters(selectSql,
				Arrays.asList(qp1), PersonDatabase::getPersonFromResultSet);
		return personList.stream().findFirst().get();
	}

	public Person getPersonDetails(Person person) throws Exception
	{
		VoteDatabase voteDb = new VoteDatabase();
		GameDatabase gameDb = new GameDatabase();
		List<Vote> votes = voteDb.getVotesForPerson(person.getPersonKey());
		List<Game> gameList = gameDb.getGameList();
		if(votes == null) return person;
		for(Vote vote : votes)
		{
			Optional<Game> game = gameList.stream().filter(g -> g.getGameKey().equals(vote.getGameKey())).findFirst();
			if(!game.isPresent())
			{
				continue;
			}
			if(vote.getVoteNumber() == 1)
			{
				person.setGameVote1(game.get().getGameName());
			}

			if(vote.getVoteNumber() == 2)
			{
				person.setGameVote2(game.get().getGameName());
			}

			if(vote.getVoteNumber() == 3)
			{
				person.setGameVote3(game.get().getGameName());
			}
		}
		return person;
	}
	
	public List<Person> getPersonList() throws Exception
	{
		String selectSql = "SELECT * FROM Person;";

		List<Person> personList = DBConnection.queryWithParameters(
				selectSql, new ArrayList<>(), PersonDatabase::getPersonFromResultSet);

		for(Person person : personList)
		{
			getPersonDetails(person);
		}

		return personList;
	}
	
	public Long getPersonKey(String personName) throws Exception
	{
		String sql = "SELECT PersonKey FROM Person WHERE PersonData->'$.PersonName' like ?;";
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

		QueryParameter qp = new QueryParameter("%" + tempName.trim(), Types.VARCHAR);
		List<Long> keys = DBConnection.queryWithParameters(sql, Arrays.asList(qp), PersonDatabase::getFirstColumn);
		return keys.stream().findFirst().get();
	}


	public void insertSuggestion(Suggestion sug) throws Exception
	{
		QueryParameter qp1 = new QueryParameter(UUID.randomUUID().toString(), Types.VARCHAR);
		QueryParameter qp2 = new QueryParameter(_gson.toJson(sug), Types.OTHER);

		DBConnection.executeWithParams(LanoelSql.INSERT_SUGGESTION, Arrays.asList(qp1, qp2));
	}
	
	public Suggestion updateSuggestion(Suggestion sug) throws Exception
	{
		QueryParameter qp1 = new QueryParameter(sug.Description, Types.VARCHAR);
		QueryParameter qp2 = new QueryParameter(sug.Category, Types.VARCHAR);
		QueryParameter qp3 = new QueryParameter(sug.Key, Types.VARCHAR);

		DBConnection.executeWithParams(LanoelSql.INSERT_SUGGESTION, Arrays.asList(qp1, qp2, qp3));

		return sug;
	}
	
	public List<Suggestion> getSuggestions() throws Exception
	{
		String sql = "SELECT * FROM Suggestion;";
		return DBConnection.queryWithParameters(sql, new ArrayList<>(), PersonDatabase::getSuggestionFromResultSet);
	}

	public static Person getPersonFromResultSet(ResultSet rs)
	{
		try
		{
			Person person = _gson.fromJson(rs.getString("PersonData"), Person.class);
			person.setPersonKey(rs.getLong("PersonKey"));
			return person;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Long getFirstColumn(ResultSet rs)
	{
		try
		{
			return rs.getLong(1);
		} catch (Exception e)
		{
			return 0L;
		}
	}

	public static Suggestion getSuggestionFromResultSet(ResultSet rs)
	{
		try
		{
			Suggestion sug = new Suggestion();
			sug.Key = rs.getString("SuggestionKey");
			sug.Description = rs.getString("Description");
			sug.Category = rs.getString("Category");
			return sug;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}

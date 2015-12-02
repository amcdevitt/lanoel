package computer.lanoel.platform;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Round;
import computer.lanoel.contracts.Tournament;
import computer.lanoel.contracts.Vote;

public class DatabaseManager {
	
	//private final String url = "jdbc:mysql://address=(protocol=tcp)(host=2001:4860:4864:1:4195:e4a4:967b:d9fd)(port=3306)";
	private String _url;
	private String _username;
	private String _password;
	
	public DatabaseManager(String url, String userName, String password)
	{
		_url = url;
		_username = userName;
		_password = password;
	}
	
	public boolean storageAvailable()
	{
		try {
			Connection conn = getDBConnection();
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
		Connection conn = getDBConnection();
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

	private Connection getDBConnection() throws SQLException
	{
		DriverManager.setLoginTimeout(5);
		Connection conn = DriverManager.getConnection(_url, _username, _password);
		conn.setAutoCommit(false);
		
		return conn;
	}
	
	public Long insertPerson(Person person) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(getPersonInsertSql(), Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			ps.setString(i++, person.getPersonName());
			ps.setString(i++, person.getTitle());
			ps.setString(i++, person.getInformation());
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
	}
	
	public Long insertVote(Vote vote) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(getVoteInsertSql(), Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			ps.setLong(i++, vote.getPersonKey());
			ps.setLong(i++, vote.getGameKey());
			ps.setInt(i++, vote.getVoteNumber());
			ps.executeUpdate();
			
			
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	vote.setVoteKey(generatedKeys.getLong(1));
	            }
	            else {
	                throw new SQLException("Item price insert failed, no ID obtained.");
	            }
	        }
			
			conn.commit();
			return vote.getVoteKey();
		}
	}
	
	public Long insertGame(Game game) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(getGameInsertSql(), Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			ps.setString(i++, game.getGameName());
			ps.setString(i++, game.getLocation());
			ps.setString(i++, game.getRules());
			ps.executeUpdate();
			
			
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	game.setGameKey(generatedKeys.getLong(1));
	            }
	            else {
	                throw new SQLException("Item price insert failed, no ID obtained.");
	            }
	        }
			
			conn.commit();
			return game.getGameKey();
		}
	}
	
	public Long updatePerson(Person person) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(getPersonUpdateSql());
			
			int i = 1;
			ps.setString(i++, person.getPersonName());
			ps.setString(i++, person.getTitle());
			ps.setString(i++, person.getInformation());
			ps.setLong(i++, person.getPersonKey());
			ps.executeUpdate();
			
			conn.commit();
			return person.getPersonKey();
		}
	}
	
	public Long updateGame(Game game) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(getGameUpdateSql());
			
			int i = 1;
			ps.setString(i++, game.getGameName());
			ps.setString(i++, game.getLocation());
			ps.setString(i++, game.getRules());
			ps.setLong(i++, game.getGameKey());
			ps.executeUpdate();
			
			conn.commit();
			return game.getGameKey();
		} catch (Exception e)
		{
			throw e;
		}
	}
	
	public Long updateVote(Vote vote) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(getVoteUpdateSql());
			
			int i = 1;
			ps.setLong(i++, vote.getPersonKey());
			ps.setLong(i++, vote.getGameKey());
			ps.setInt(i++, vote.getVoteNumber());
			ps.setLong(i++, vote.getVoteKey());
			ps.executeUpdate();
			
			conn.commit();
			return vote.getVoteKey();
		}
	}
	
	public List<Vote> getVotesForPerson(Long personKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			if(personKey == null) return null;
			
			String selectSql = "SELECT * FROM Vote WHERE PersonKey = ?;";
			PreparedStatement ps = conn.prepareStatement(selectSql);
			ps.setLong(1, personKey);
			ResultSet rs = ps.executeQuery();
	
			if(!rs.isBeforeFirst()) return null;
			
			List<Vote> voteListToReturn = new ArrayList<Vote>();
			while(rs.next())
			{
				Vote vote = new Vote();
				vote.setPersonKey(rs.getLong("PersonKey"));
				vote.setGameKey(rs.getLong("GameKey"));
				vote.setVoteNumber(rs.getInt("VoteNumber"));
				vote.setVoteKey(rs.getLong("VoteKey"));
				voteListToReturn.add(vote);
			}
			return voteListToReturn;
		}
	}
	
	
	private String getPersonUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE Person SET ");
		
		updateSql.append("PersonName=?,");
		updateSql.append("Title=?,");
		updateSql.append("Information=?");
		
		whereSql.append(" WHERE PersonKey=?;");
		return updateSql.append(whereSql).toString();
	}
	
	private String getGameUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE Game SET ");
		
		updateSql.append("GameName=?,");
		updateSql.append("Location=?,");
		updateSql.append("Rules=?");
		
		whereSql.append(" WHERE GameKey=?;");
		return updateSql.append(whereSql).toString();
	}
	
	private String getVoteUpdateSql()
	{
		StringBuilder updateSql = new StringBuilder();
		StringBuilder whereSql = new StringBuilder();
		updateSql.append("UPDATE Vote SET ");
		
		updateSql.append("PersonKey=?,");
		updateSql.append("GameKey=?,");
		updateSql.append("VoteNumber=?");
		
		whereSql.append(" WHERE VoteKey=?;");
		return updateSql.append(whereSql).toString();
	}
	
	private String getPersonInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO Person (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("PersonName,");
		valuesSql.append("?,");
		insertSql.append("Title,");
		valuesSql.append("?,");
		insertSql.append("Information");
		valuesSql.append("?");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	private String getGameInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO Game (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("GameName,");
		valuesSql.append("?,");
		insertSql.append("Location,");
		valuesSql.append("?,");
		insertSql.append("Rules");
		valuesSql.append("?");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	private String getVoteInsertSql()
	{
		StringBuilder insertSql = new StringBuilder();
		StringBuilder valuesSql = new StringBuilder();
		insertSql.append("INSERT INTO Vote (");
		valuesSql.append(") VALUES (");
		
		insertSql.append("PersonKey,");
		valuesSql.append("?,");
		insertSql.append("GameKey,");
		valuesSql.append("?,");
		insertSql.append("VoteNumber");
		valuesSql.append("?");
		
		valuesSql.append(");");
		return insertSql.append(valuesSql).toString();
	}
	
	public void deleteRecord(Long primaryKey, String tableName) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			String deleteSql = "DELETE FROM Person WHERE " + tableName + " = ?";
			PreparedStatement ps = conn.prepareStatement(deleteSql);
			ps.setLong(1, primaryKey);
			ps.executeUpdate();
			conn.commit();
		}
	}
	
	public Person getPerson(Long personKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			if(personKey == null) return null;
			
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
			}
			
			List<Vote> votes = getVotesForPerson(personToReturn.getPersonKey());
			if(votes == null) return personToReturn;
			for(Vote vote : votes)
			{
				if(vote.getVoteNumber() == 1)
				{
					personToReturn.setGameVote1(getGame(vote.getGameKey()).getGameName());
				}
				
				if(vote.getVoteNumber() == 2)
				{
					personToReturn.setGameVote2(getGame(vote.getGameKey()).getGameName());
				}
				
				if(vote.getVoteNumber() == 3)
				{
					personToReturn.setGameVote3(getGame(vote.getGameKey()).getGameName());
				}
			}
			
			return personToReturn;
		}
	}
	
	public Vote getVote(Long voteKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			if(voteKey == null) return null;
			
			String selectSql = "SELECT * FROM Vote WHERE VoteKey = ?";
			PreparedStatement ps = conn.prepareStatement(selectSql);
			ps.setLong(1, voteKey);
			ResultSet rs = ps.executeQuery();
	
			if(!rs.isBeforeFirst()) return null; //No results
			
			Vote voteToReturn = new Vote();
			while(rs.next())
			{
				voteToReturn.setPersonKey(rs.getLong("PersonKey"));
				voteToReturn.setGameKey(rs.getLong("GameKey"));
				voteToReturn.setVoteNumber(rs.getInt("VoteNumber"));
				voteToReturn.setVoteKey(rs.getLong("VoteKey"));
			}
			return voteToReturn;
		}
	}
	
	public Game getGame(Long gameKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			if(gameKey == null) return null;
			
			String selectSql = "SELECT * FROM Game WHERE GameKey = ?";
			PreparedStatement ps = conn.prepareStatement(selectSql);
			ps.setLong(1, gameKey);
			ResultSet rs = ps.executeQuery();
	
			if(!rs.isBeforeFirst()) return null; //No results
			
			Game gameToReturn = new Game();
			while(rs.next())
			{
				gameToReturn.setGameKey(rs.getLong("GameKey"));
				gameToReturn.setGameName(rs.getString("GameName"));
				gameToReturn.setLocation(rs.getString("Location"));
				gameToReturn.setRules(rs.getString("Rules"));
				
				String voteSql = "SELECT * FROM Vote where GameKey = ?;";
				PreparedStatement ps2 = conn.prepareStatement(voteSql);
				ps2.setLong(1, gameToReturn.getGameKey());
				ResultSet rs2 = ps2.executeQuery();
				
				if(!rs2.isBeforeFirst()) continue;
				
				int total = 0;
				
				while(rs2.next())
				{
					total += rs2.getInt("VoteNumber");
				}
				
				gameToReturn.setVoteTotal(total);
			}
			return gameToReturn;
		}
	}
	
	public List<Game> getGameList() throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			String selectSql = "SELECT * FROM Game;";
			PreparedStatement ps = conn.prepareStatement(selectSql);
			ResultSet rs = ps.executeQuery();
	
			if(!rs.isBeforeFirst()) return null;
			
			List<Game> gameList = new ArrayList<Game>();
			while(rs.next())
			{
				Game game = new Game();
				game.setGameKey(rs.getLong("GameKey"));
				game.setGameName(rs.getString("GameName"));
				game.setLocation(rs.getString("Location"));
				game.setRules(rs.getString("Rules"));
				gameList.add(game);
				
				String voteSql = "SELECT * FROM Vote where GameKey = ?;";
				PreparedStatement ps2 = conn.prepareStatement(voteSql);
				ps2.setLong(1, game.getGameKey());
				ResultSet rs2 = ps2.executeQuery();
				
				if(!rs2.isBeforeFirst()) continue;
				
				int total = 0;
				
				while(rs2.next())
				{
					total += rs2.getInt("VoteNumber");
				}
				
				game.setVoteTotal(total);
				
			}
			return gameList;
		}
	}
	
	public List<Person> getPersonList() throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			String selectSql = "SELECT * FROM Person;";
			PreparedStatement ps = conn.prepareStatement(selectSql);
			ResultSet rs = ps.executeQuery();
	
			if(!rs.isBeforeFirst()) return null;
			
			List<Person> personList = new ArrayList<Person>();
			while(rs.next())
			{
				personList.add(getPerson(rs.getLong("PersonKey")));				
			}
			return personList;
		}
	}
	
	public List<Game> getTopFiveGames() throws Exception
	{
		List<Game> gameList = getGameList();
		
		try(Connection conn = getDBConnection())
		{
			String gamesByUniquePersonVotesSql = "select GameKey, count(distinct PersonKey) as UniqueVotes "
					+ "from Vote group by GameKey order by count(distinct PersonKey) desc;";
			
			PreparedStatement ps = conn.prepareStatement(gamesByUniquePersonVotesSql);
			ResultSet rs = ps.executeQuery();
	
			if(!rs.isBeforeFirst()) return null;
			
			while(rs.next())
			{
				Long gameKey = rs.getLong("GameKey");
				for(Game game : gameList)
				{
					if(game.getGameKey() == gameKey)
					{
						game.setNumUniquePersonVotes(rs.getInt("UniqueVotes"));
					}
				}
			}
			
			Collections.sort(gameList);
			
			return gameList.subList(0, 5);
		}
	}
	
	/*********************Tournament****************************/
	
	public Long insertTournament(Tournament tourn) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(TournamentSql.tournamentInsertSql(), Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			ps.setString(i++, tourn.getTournamentName());
			ps.executeUpdate();
			
			
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                tourn.setTournamentKey(generatedKeys.getLong(1));
	            }
	            else {
	                throw new SQLException("Item price insert failed, no ID obtained.");
	            }
	        }
			
			conn.commit();
			return tourn.getTournamentKey();
		}
	}
	
	public void updateTournament(Tournament tourn) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(TournamentSql.tournamentUpdateSql());
			
			int i = 1;
			ps.setString(i++, tourn.getTournamentName());
			ps.setLong(i++, tourn.getTournamentKey());
			ps.executeUpdate();
			
			conn.commit();
		}
	}
	
	public Long insertRound(Long tournamentKey, Round round) throws Exception
	{
		if(round.getGame() == null)
		{
			throw new Exception("Must set a game!!!");
		}
		
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(TournamentSql.roundInsertSql(), Statement.RETURN_GENERATED_KEYS);
			
			int i = 1;
			ps.setLong(i++, tournamentKey);
			ps.setInt(i++, round.getRoundNumber());
			ps.setLong(i++, round.getGame().getGameKey());
			ps.executeUpdate();
			
			
			try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                round.setRoundKey(generatedKeys.getLong(1));
	            }
	            else {
	                throw new SQLException("Item price insert failed, no ID obtained.");
	            }
	        }
			
			conn.commit();
			return round.getRoundKey();
		}
	}
	
	public void updateRound(Long tournamentKey, Round round) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement(TournamentSql.roundUpdateSql());
			
			int i = 1;
			ps.setLong(i++, round.getGame().getGameKey());
			ps.setLong(i++, tournamentKey);
			ps.setLong(i++, round.getRoundNumber());
			ps.executeUpdate();
			
			conn.commit();
		}
	}
	
	public void insertRoundStanding(Long personKey, Long roundKey, int place) throws Exception
	{		
		try(Connection conn = getDBConnection())
		{
			try
			{
				PreparedStatement deletePs = conn.prepareStatement("DELETE FROM RoundStanding WHERE RoundKey=? AND PersonKey=?");
				deletePs.setLong(1, roundKey);
				deletePs.setLong(2, personKey);
				deletePs.execute();
			} catch (Exception e)
			{
				// Do nothing!
			}
			
			
			PreparedStatement ps = conn.prepareStatement(TournamentSql.roundStandingInsertSql());
			
			int i = 1;
			ps.setLong(i++, roundKey);
			ps.setLong(i++, personKey);
			ps.setInt(i++, place);
			ps.executeUpdate();

			conn.commit();
		}
	}
	
	public void replaceRoundStandings(Long roundKey, List<Place> places) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			resetRoundStandings(roundKey);
			
			for(Place place : places)
			{
				PreparedStatement ps = conn.prepareStatement(TournamentSql.roundStandingUpdateSql());
				
				int i = 1;
				ps.setInt(i++, place.getPlace());
				ps.setLong(i++, roundKey);
				ps.setLong(i++, getPersonKey(place.getPerson()));
				ps.executeUpdate();
			}
			
			conn.commit();
		}
	}
	
	public void resetRoundStandings(Long roundKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			try
			{
				PreparedStatement deletePs = conn.prepareStatement("DELETE FROM RoundStanding WHERE RoundKey=?");
				deletePs.setLong(1, roundKey);
				deletePs.execute();
				conn.commit();
			} catch (Exception e)
			{
				// Do nothing!
			}
			
			//Get all person's and put them in the round at 99th place
			List<Person> personList = getPersonList();
			for(Person person : personList)
			{
				insertRoundStanding(person.getPersonKey(), roundKey, 99);
			}
		}
	}
	
	public Long getPersonKey(String personName) throws Exception
	{
		try(Connection conn = getDBConnection())
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
		}
		
		return null;
	}
	
	public Tournament getTournament(Long tournamentKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Tournament WHERE TournamentKey = ?;");
			
			ps.setLong(1, tournamentKey);
			
			ResultSet rs = ps.executeQuery();
			
			if(!rs.isBeforeFirst()) return null;
			
			Tournament tourn = new Tournament();
			
			while(rs.next())
			{
				tourn.setTournamentName(rs.getString("TournamentName"));
				tourn.setTournamentKey(rs.getLong("TournamentKey"));
				tourn.setRounds(getRounds(tournamentKey));
			}
			
			return tourn;
		}
	}
	
	public List<Round> getRounds(Long tournamentKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM Round WHERE TournamentKey = ?;");
			
			ps.setLong(1, tournamentKey);
			
			ResultSet rs = ps.executeQuery();
			
			if(!rs.isBeforeFirst()) return new ArrayList<Round>();
			
			List<Round> roundList = new ArrayList<Round>();
			
			while(rs.next())
			{
				Round currentRound = new Round();
				Long gameKey = rs.getLong("GameKey");
				currentRound.setRoundNumber(rs.getInt("RoundNumber"));
				currentRound.setRoundKey(rs.getLong("RoundKey"));
				currentRound.setGame(getGame(gameKey));
				currentRound.setPlaces(getRoundStandings(currentRound.getRoundKey()));
				roundList.add(currentRound);
			}
			
			return roundList;
		}
	}
	
	public List<Place> getRoundStandings(Long roundKey) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM RoundStanding WHERE RoundKey = ?;");
			
			ps.setLong(1, roundKey);
			
			ResultSet rs = ps.executeQuery();
			
			if(!rs.isBeforeFirst()) return new ArrayList<Place>();
			
			List<Place> standingList = new ArrayList<Place>();
			
			while(rs.next())
			{
				Place currentPlace = new Place();
				Long personKey = rs.getLong("PersonKey");
				Person currentPerson = getPerson(personKey);
				if(currentPerson == null) continue;
				int place = rs.getInt("Place");
				String scoreName = (String) (currentPerson.getTitle() == null ? " " : currentPerson.getTitle());
				scoreName += ' ' + currentPerson.getPersonName();
				
				currentPlace.setPerson(scoreName.trim());
				currentPlace.setPlace(place);
				
				standingList.add(currentPlace);
			}
			
			return standingList;
		}
	}
	
	public Map<Integer, Integer> getPointValues() throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			PreparedStatement ps = conn.prepareStatement("SELECT * FROM PointValues");
			
			ResultSet rs = ps.executeQuery();
			
			if(!rs.isBeforeFirst()) return null;
			
			Map<Integer, Integer> pointMap = new HashMap<Integer, Integer>();
			
			while(rs.next())
			{
				int place = rs.getInt("Place");
				int pointValue = rs.getInt("PointValue");
				
				pointMap.put(place, pointValue);
			}
			
			return pointMap;
		}
	}
	
	public void updatePointValues(Map<Integer, Integer> pointMap) throws Exception
	{
		try(Connection conn = getDBConnection())
		{
			Iterator it = pointMap.entrySet().iterator();
			
			while(it.hasNext())
			{
				PreparedStatement ps = conn.prepareStatement("UPDATE PointValues SET PointValue=? WHERE Place=?;");
				Map.Entry<Integer, Integer> pair = (Map.Entry)it.next();
				ps.setInt(1, pair.getValue());
				ps.setInt(2, pair.getKey());
				it.remove();
				
				ps.executeUpdate();
				conn.commit();
				
			}
		}
	}
}

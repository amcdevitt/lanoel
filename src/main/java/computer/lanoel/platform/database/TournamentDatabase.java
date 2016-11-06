package computer.lanoel.platform.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Round;
import computer.lanoel.contracts.Tournament;
import computer.lanoel.platform.database.sql.TournamentSql;

public class TournamentDatabase extends DatabaseManager implements IDatabase {

	public TournamentDatabase(Connection connection) {
		super(connection);
	}

	public Long insertTournament(Tournament tourn) throws Exception
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
	
	public void updateTournament(Tournament tourn) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(TournamentSql.tournamentUpdateSql());
		
		int i = 1;
		ps.setString(i++, tourn.getTournamentName());
		ps.setLong(i++, tourn.getTournamentKey());
		ps.executeUpdate();
		
		conn.commit();
	}
	
	public Long insertRound(Long tournamentKey, Round round) throws Exception
	{
		if(round.getGame() == null)
		{
			throw new Exception("Must set a game!!!");
		}
		
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
		conn.close();
		return round.getRoundKey();
	}
	
	public void updateRound(Long tournamentKey, Round round) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(TournamentSql.roundUpdateSql());
		
		int i = 1;
		ps.setLong(i++, round.getGame().getGameKey());
		ps.setLong(i++, tournamentKey);
		ps.setLong(i++, round.getRoundNumber());
		ps.executeUpdate();
		
		conn.commit();
		conn.close();
	}
	
	public void insertRoundStanding(Long personKey, Long roundKey, int place) throws Exception
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
	
	public void replaceRoundStandings(Long roundKey, List<Place> places) throws Exception
	{
		resetRoundStandings(roundKey);
		
		PersonDatabase personDb = (PersonDatabase) DatabaseFactory.getInstance().getDatabase("PERSON", conn);
		
		for(Place place : places)
		{
			PreparedStatement ps = conn.prepareStatement(TournamentSql.roundStandingUpdateSql());
			
			int i = 1;
			ps.setInt(i++, place.getPlace());
			ps.setLong(i++, roundKey);
			ps.setLong(i++, personDb.getPersonKey(place.getPerson()));
			ps.executeUpdate();
		}
		
		conn.commit();
	}
	
	public void resetRoundStandings(Long roundKey) throws Exception
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
		
		PersonDatabase personDb = (PersonDatabase) DatabaseFactory.getInstance().getDatabase("PERSON", conn);
		
		//Get all person's and put them in the round at 99th place
		List<Person> personList = personDb.getPersonList();
		for(Person person : personList)
		{
			insertRoundStanding(person.getPersonKey(), roundKey, 99);
		}
	}
	
	public Tournament getTournament(Long tournamentKey) throws Exception
	{
		PreparedStatement tournamentPs = conn.prepareStatement("SELECT * FROM Tournament WHERE TournamentKey = ?;");
		PreparedStatement roundPs = conn.prepareStatement("SELECT * FROM Round WHERE TournamentKey = ?;");
		PreparedStatement roundStandingPs = conn.prepareStatement("SELECT * FROM RoundStanding;");
		PreparedStatement gamePs = conn.prepareStatement("SELECT * FROM Game;");
		PreparedStatement personPs = conn.prepareStatement("SELECT * FROM Person;");
		
		tournamentPs.setLong(1, tournamentKey);
		roundPs.setLong(1, tournamentKey);
		
		ResultSet tournamentRs = tournamentPs.executeQuery();
		ResultSet roundRs = roundPs.executeQuery();
		ResultSet roundStandingRs = roundStandingPs.executeQuery();
		ResultSet gameRs = gamePs.executeQuery();
		ResultSet personRs = personPs.executeQuery();
		
		// Tournament
		if(!tournamentRs.isBeforeFirst()) return null;
		
		Tournament tourn = new Tournament();
		
		while(tournamentRs.next())
		{
			tourn.setTournamentName(tournamentRs.getString("TournamentName"));
			tourn.setTournamentKey(tournamentRs.getLong("TournamentKey"));
			//tourn.setRounds(getRounds(tournamentKey));
		}
		
		//Person
		if(!personRs.isBeforeFirst()) return null; //No results
		
		List<Person> personList = new ArrayList<Person>();
		while(personRs.next())
		{
			Person personToAdd = new Person();
			personToAdd.setPersonKey(personRs.getLong("PersonKey"));
			personToAdd.setPersonName(personRs.getString("PersonName"));
			personToAdd.setTitle(personRs.getString("Title"));
			personToAdd.setInformation(personRs.getString("Information"));
			personList.add(personToAdd);
		}
		
		//Standings
		List<Place> standingList = new ArrayList<Place>();
		Map<Integer, Integer> pointValues = getPointValues();
		
		while(roundStandingRs.next())
		{
			Place currentPlace = new Place();
			Long personKey = roundStandingRs.getLong("PersonKey");
			Long roundKey = roundStandingRs.getLong("RoundKey");
			Person currentPerson = personList.stream().filter(p -> p.getPersonKey() == personKey).collect(Collectors.toList()).get(0);
			if(currentPerson == null) continue;
			int place = roundStandingRs.getInt("Place");
			String scoreName = (String) (currentPerson.getTitle() == null ? " " : currentPerson.getTitle());
			scoreName += ' ' + currentPerson.getPersonName();
			
			currentPlace.setPerson(scoreName.trim());
			currentPlace.setRoundKey(roundKey);
			
			int pointValue = 0;
			
			try
			{
				pointValue = pointValues.get(place);
			} catch (Exception e)
			{
				// Do nothing
			}
			
			currentPlace.setPlace(place, pointValue);
			
			standingList.add(currentPlace);
		}
		
		//Games
		if(!gameRs.isBeforeFirst()) return null;
		
		List<Game> gameList = new ArrayList<Game>();
		while(gameRs.next())
		{
			Game game = new Game();
			game.setGameKey(gameRs.getLong("GameKey"));
			game.setGameName(gameRs.getString("GameName"));
			game.setLocation(gameRs.getString("Location"));
			game.setRules(gameRs.getString("Rules"));
			gameList.add(game);
		}
		
		//Rounds
		List<Round> roundList = new ArrayList<Round>();
		
		while(roundRs.next())
		{
			Round currentRound = new Round();
			Long gameKey = roundRs.getLong("GameKey");
			currentRound.setRoundNumber(roundRs.getInt("RoundNumber"));
			currentRound.setRoundKey(roundRs.getLong("RoundKey"));
			Game tempGame = new Game();
			tempGame.setGameKey(gameKey);
			currentRound.setGame(gameList.get(gameList.indexOf(tempGame)));
			
			List<Place> placeList = new ArrayList<Place>();
			for(Place place : standingList)
			{
				if(place.getRoundKey() == currentRound.getRoundKey())
				{
					placeList.add(place);
				}
			}
			
			currentRound.setPlaces(placeList);
			roundList.add(currentRound);
		}
		
		tourn.setRounds(roundList);
		
		return tourn;
	}
	
	public List<Round> getRounds(Long tournamentKey) throws Exception
	{
		GameDatabase gameDb = (GameDatabase) DatabaseFactory.getInstance().getDatabase("GAME", conn);
		
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
			currentRound.setGame(gameDb.getGame(gameKey));
			currentRound.setPlaces(getRoundStandings(currentRound.getRoundKey()));
			roundList.add(currentRound);
		}
		
		return roundList;
	}
	
	public List<Place> getRoundStandings(Long roundKey) throws Exception
	{
		PersonDatabase personDb = (PersonDatabase) DatabaseFactory.getInstance().getDatabase("PERSON", conn);
		PreparedStatement ps = conn.prepareStatement("SELECT * FROM RoundStanding WHERE RoundKey = ?;");
		
		ps.setLong(1, roundKey);
		
		ResultSet rs = ps.executeQuery();
		
		if(!rs.isBeforeFirst()) return new ArrayList<Place>();
		
		List<Place> standingList = new ArrayList<Place>();
		
		while(rs.next())
		{
			Place currentPlace = new Place();
			Long personKey = rs.getLong("PersonKey");
			Person currentPerson = personDb.getPerson(personKey);
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
	
	public void updatePointValues(Map<Integer, Integer> pointMap) throws Exception
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
	
	public Map<Integer, Integer> getPointValues() throws Exception
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

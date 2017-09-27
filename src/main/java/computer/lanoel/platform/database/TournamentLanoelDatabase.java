package computer.lanoel.platform.database;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Tournaments.Lanoel.Round;
import computer.lanoel.contracts.Tournaments.Lanoel.TournamentLanoel;

import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import computer.lanoel.platform.database.sql.TournamentLanoelSql;

public class TournamentLanoelDatabase extends TournamentDatabase {

	private static Gson _gson;
	public TournamentLanoelDatabase() {
		_gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
	}

	public Long insertTournament(TournamentLanoel tourn) throws Exception
	{
		return super.createTournament(tourn, "LANOEL").tournamentKey;
	}

	public TournamentParticipant addParticipant(Long tournamentKey, TournamentParticipant part) throws SQLException
	{
		return super.addParticipant(tournamentKey, part);
	}

	public TournamentParticipant updateParticipant(TournamentParticipant part) throws SQLException
	{
		return super.updateParticipant(part);
	}

	public void removeParticipant(Long participantKey) throws SQLException
	{
		super.removeParticipantFromTournament(participantKey);
	}

	public void updateTournament(TournamentLanoel tourn) throws Exception
	{
		super.updateTournament(tourn);
	}
	
	public Long insertRound(Long tournamentKey, Round round) throws Exception
	{
		if(round.getGame() == null)
		{
			throw new Exception("Must set a game!!!");
		}

		QueryParameter qp1 = new QueryParameter(tournamentKey, Types.BIGINT);
		QueryParameter qp2 = new QueryParameter(round.getRoundNumber(), Types.INTEGER);
		QueryParameter qp3 = new QueryParameter(round.getGame().getGameKey(), Types.BIGINT);

		return DBConnection.executeUpdateReturnGeneratedKey(TournamentLanoelSql.roundInsertSql(), Arrays.asList(qp1, qp2, qp3));
	}
	
	public void updateRound(Long tournamentKey, Round round) throws Exception
	{
		QueryParameter qp1 = new QueryParameter(round.getGame().getGameKey(), Types.BIGINT);
		QueryParameter qp2 = new QueryParameter(tournamentKey, Types.BIGINT);
		QueryParameter qp3 = new QueryParameter(round.getRoundNumber(), Types.INTEGER);

		DBConnection.executeWithParams(TournamentLanoelSql.roundUpdateSql(), Arrays.asList(qp1, qp2, qp3));
	}
	
	public void insertRoundStanding(Long participantKey, Long roundKey, int place) throws Exception
	{
		String deleteSql = "DELETE FROM TournamentLanoel_RoundStanding WHERE RoundKey=? AND ParticipantKey=?";
		QueryParameter qp1 = new QueryParameter(roundKey, Types.BIGINT);
		QueryParameter qp2 = new QueryParameter(participantKey, Types.BIGINT);
		DBConnection.executeWithParams(deleteSql, Arrays.asList(qp1, qp2));

		QueryParameter qp3 = new QueryParameter(place, Types.INTEGER);
		DBConnection.executeUpdateWithParams(TournamentLanoelSql.roundStandingInsertSql(), Arrays.asList(qp1, qp2, qp3));
	}
	
	public void replaceRoundStandings(Long roundKey, List<Place> places) throws Exception
	{
		resetRoundStandings(roundKey);

		for(Place place : places) {
			QueryParameter qp1 = new QueryParameter(roundKey, Types.BIGINT);
			QueryParameter qp2 = new QueryParameter(place.getParticipant().tournamentParticipantKey, Types.BIGINT);
			QueryParameter qp3 = new QueryParameter(place.getPlace(), Types.INTEGER);
			DBConnection.executeUpdateWithParams(TournamentLanoelSql.roundStandingInsertSql(), Arrays.asList(qp1, qp2, qp3));
		}
	}
	
	public void resetRoundStandings(Long roundKey) throws Exception
	{
		String deleteSql = "DELETE FROM TournamentLanoel_RoundStanding WHERE RoundKey=?";
		QueryParameter qp = new QueryParameter(roundKey, Types.BIGINT);
		DBConnection.executeWithParams(deleteSql, Arrays.asList(qp));

		PersonDatabase personDb = new PersonDatabase();
		
		//Get all person's and put them in the round at 99th place
		List<Person> personList = personDb.getPersonList();
		for(Person person : personList)
		{
			insertRoundStanding(person.getPersonKey(), roundKey, 99);
		}
	}

	public TournamentLanoel getTournament(Long tournamentKey) throws SQLException
	{
		TournamentDatabase tdb = new TournamentDatabase();
		List<Round> roundList = getRounds(tournamentKey);

		TournamentLanoel tourn = (TournamentLanoel)tdb.getTournamentList(TournamentLanoelDatabase::getTournamentFromResultSet).stream()
				.filter(t -> t.tournamentKey.equals(tournamentKey)).findFirst().get();

		tourn.setRounds(roundList);
		
		return tourn;
	}
	
	public List<Round> getRounds(Long tournamentKey) throws SQLException
	{
		String roundSql = "SELECT r.*, g.GameData " +
				"FROM TournamentLanoel_Round r " +
				"join Game g on g.GameKey = r.GameKey " +
				"WHERE TournamentKey = ?";
		String roundStandingSql = "select rs.*, p.TournamentParticipantData " +
				"from TournamentLanoel_RoundStanding rs " +
				"join TournamentLanoel_Round r on r.RoundKey = rs.RoundKey " +
				"join TournamentParticipant p on p.TournamentParticipantKey = rs.ParticipantKey " +
				"where r.TournamentKey = ?";
		GameDatabase gameDb = new GameDatabase();
		List<Game> gameList = gameDb.getGameList();

		QueryParameter qp = new QueryParameter(tournamentKey, Types.BIGINT);
		List<Round> roundList = DBConnection.queryWithParameters(roundSql,
				Arrays.asList(qp), TournamentLanoelDatabase::getRoundFromResultSet);
		List<Place> roundStandingList = DBConnection.queryWithParameters(roundStandingSql,
				Arrays.asList(qp), TournamentLanoelDatabase::getRoundStandingFromResultSet);

		for(Round round : roundList)
		{
			round.setPlaces(roundStandingList.stream().filter(rs -> rs.getRoundKey().equals(round.getRoundKey()))
					.collect(Collectors.toList()));
			round.setGame(gameList.stream().filter(g -> g.getGameKey().equals(round.gameKey)).findFirst().get());
		}
		
		return roundList;
	}
	
	public void updatePointValues(Map<Integer, Integer> pointMap) throws Exception
	{
		Iterator it = pointMap.entrySet().iterator();
		String sql = "UPDATE TournamentLanoel_PointValues SET PointValue=? WHERE Place=?;";
		QueryBatch qb = new QueryBatch();

		while(it.hasNext())
		{
			Map.Entry<Integer, Integer> pair = (Map.Entry)it.next();
			QueryParameter qp1 = new QueryParameter(pair.getValue(), Types.INTEGER);
			QueryParameter qp2 = new QueryParameter(pair.getKey(), Types.INTEGER);
			it.remove();
			
			qb.addBatch(Arrays.asList(qp1, qp2));
		}
		DBConnection.executeBatch(sql, qb);
	}
	
	public Map<Integer, Integer> getPointValues() throws Exception
	{
		String sql = "SELECT * FROM TournamentLanoel_PointValues";
		List<Map.Entry<Integer, Integer>> entryList =
				DBConnection.queryWithParameters(sql, new ArrayList<>(), TournamentLanoelDatabase::getPointValuesFromResultSet);

		Map<Integer, Integer> pointValueMap = new HashMap<>();
		for(Map.Entry<Integer, Integer> entry : entryList)
		{
			pointValueMap.put(entry.getKey(), entry.getValue());
		}
		return pointValueMap;
	}

	public static TournamentLanoel getTournamentFromResultSet(ResultSet rs)
	{
		try
		{
			TournamentLanoel t = _gson.fromJson(rs.getString("TournamentData"), TournamentLanoel.class);
			t.tournamentKey = rs.getLong("TournamentKey");
			return t;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Place getRoundStandingFromResultSet(ResultSet rs)
	{
		try
		{
			Place currentPlace = new Place();
			TournamentParticipant currentPerson = _gson.fromJson(rs.getString("TournamentParticipantData"), TournamentParticipant.class);
			currentPlace.setPlace(rs.getInt("Place"));
			currentPlace.setParticipant(currentPerson);
			return currentPlace;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Round getRoundFromResultSet(ResultSet rs)
	{
		try
		{
			Round currentRound = new Round();
			currentRound.setRoundNumber(rs.getInt("RoundNumber"));
			currentRound.setRoundKey(rs.getLong("RoundKey"));
			currentRound.setGame(_gson.fromJson(rs.getString("GameData"), Game.class));
			currentRound.gameKey = rs.getLong("GameKey");
			return currentRound;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Map.Entry<Integer, Integer> getPointValuesFromResultSet(ResultSet rs)
	{
		try
		{
			int place = rs.getInt("Place");
			int pointValue = rs.getInt("PointValue");
			return new AbstractMap.SimpleEntry<>(place, pointValue);
		} catch (SQLException e)
		{
			e.printStackTrace();
			return new AbstractMap.SimpleEntry<>(null, null);
		}
	}
}

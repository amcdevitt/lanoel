package computer.lanoel.platform.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.database.sql.LanoelSql;
import org.springframework.beans.factory.annotation.Autowired;

import javax.management.Query;
import javax.sql.DataSource;

public class GameDatabase{

	private static Gson _gson;
	public GameDatabase() {
		_gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
	}

	public Long insertGame(Game game) throws Exception
	{
		QueryParameter qp = new QueryParameter(_gson.toJson(game), Types.OTHER);
		return DBConnection.executeUpdateReturnGeneratedKey(LanoelSql.INSERT_GAME, Arrays.asList(qp));
	}
	
	public Long updateGame(Game game) throws Exception
	{
		QueryParameter qp = new QueryParameter(_gson.toJson(game), Types.OTHER);
		QueryParameter qp2 = new QueryParameter(game.getGameKey(), Types.BIGINT);
		DBConnection.executeWithParams(LanoelSql.UPDATE_GAME, Arrays.asList(qp, qp2));
		return game.getGameKey();
	}
	
	public Game getGame(Long gameKey) throws Exception
	{
		return getGameList().stream().filter(g -> g.getGameKey().equals(gameKey)).findFirst().get();
	}
	
	public List<Game> getGameList() throws SQLException
	{
		String selectSql = "select g.*, " +
				"count(distinct v.PersonKey) as UniqueVotes, " +
				"sum(v.VoteNumber) as Points " +
				"from Game g " +
				"left join Vote v " +
				"on g.GameKey = v.GameKey " +
				"group by v.GameKey " +
				"order by sum(v.VoteNumber) desc, " +
				"count(distinct v.PersonKey) desc, " +
				"g.GameKey ";
		String voteSql = "SELECT * FROM Vote;";

		List<Game> gameList = DBConnection.queryWithParameters(selectSql, Arrays.asList(), GameDatabase::getGameWithUniqueVotesFromResultSet);
		List<Vote> votes = DBConnection.queryWithParameters(voteSql, new ArrayList<>(), VoteDatabase::getVoteFromResultSet);

		for(Game game : gameList)
		{
			if(null == votes || votes.size() == 0)
			{
				game.setVoteTotal(0);
				continue;
			}
			else
			{
				game.setVoteTotal(0);
				for(Vote vote : votes)
				{
					if(vote.getGameKey().equals(game.getGameKey()))
					{
						game.setVoteTotal(game.getVoteTotal() + vote.getVoteNumber());
					}
				}
			}
		}
		return gameList;
	}
	
	public List<Game> getTopFiveGames() throws Exception
	{
		String gamesByUniquePersonVotesSql = "select g.*, " +
				"count(distinct v.PersonKey) as UniqueVotes, " +
				"sum(v.VoteNumber) as Points " +
				"from Vote v " +
				"join Game g " +
				"on g.GameKey = v.GameKey " +
				"group by v.GameKey " +
				"order by sum(v.VoteNumber) desc, " +
				"count(distinct v.PersonKey) desc, " +
				"g.GameKey " +
				"limit 5;";

		return DBConnection.queryWithParameters(gamesByUniquePersonVotesSql,
				new ArrayList<>(), GameDatabase::getGameWithUniqueVotesFromResultSet);
	}

	public static Game getGameFromResultSet(ResultSet rs)
	{
		try
		{
			Game game = _gson.fromJson(rs.getString("GameData"), Game.class);
			game.setGameKey(rs.getLong("GameKey"));
			return game;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public static Game getGameWithUniqueVotesFromResultSet(ResultSet rs)
	{
		try
		{
			Game game = _gson.fromJson(rs.getString("GameData"), Game.class);
			game.setGameKey(rs.getLong("GameKey"));
			game.setNumUniquePersonVotes(rs.getInt("UniqueVotes"));
			return game;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}

package computer.lanoel.platform.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Game;
import computer.lanoel.platform.database.sql.LanoelSql;

public class GameDatabase extends DatabaseManager implements IDatabase {

	private Gson _gson;
	public GameDatabase(Connection connection) {
		super(connection);
		_gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
	}

	public Long insertGame(Game game) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.INSERT_GAME, Statement.RETURN_GENERATED_KEYS);
		
		int i = 1;
		ps.setString(i++, _gson.toJson(game));
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
	
	public Long updateGame(Game game) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.UPDATE_GAME);
		
		int i = 1;
		ps.setString(i++, _gson.toJson(game));
		ps.setLong(i++, game.getGameKey());
		ps.executeUpdate();
		
		conn.commit();
		return game.getGameKey();
	}
	
	public Game getGame(Long gameKey) throws Exception
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
			gameToReturn = _gson.fromJson(rs.getString("GameData"), Game.class);
			gameToReturn.setGameKey(rs.getLong("GameKey"));


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
	
	public List<Game> getGameList() throws Exception
	{
		String selectSql = "SELECT * FROM Game;";
		PreparedStatement ps = conn.prepareStatement(selectSql);
		ResultSet rs = ps.executeQuery();

		if(!rs.isBeforeFirst()) new ArrayList<Game>();
		
		List<Game> gameList = new ArrayList<Game>();
		while(rs.next())
		{
			Game game = _gson.fromJson(rs.getString("GameData"), Game.class);
			game.setGameKey(rs.getLong("GameKey"));
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
	
	public List<Game> getTopFiveGames() throws Exception
	{
		List<Game> gameList = getGameList();
		
		String gamesByUniquePersonVotesSql = "select g.*, count(distinct v.PersonKey) as UniqueVotes " +
				"from Vote v join Game g " +
				"on g.GameKey = v.GameKey " +
				"group by v.GameKey order by count(distinct v.PersonKey) " +
				"desc limit 5";
		
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

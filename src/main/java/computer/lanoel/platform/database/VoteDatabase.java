package computer.lanoel.platform.database;

import java.sql.*;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.database.sql.LanoelSql;

public class VoteDatabase {

	private static Gson _gson;
	public VoteDatabase()
	{
		_gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
	}

	public Long insertVote(Vote vote) throws Exception
	{
		QueryParameter qp1 = new QueryParameter(vote.getPersonKey(), Types.BIGINT);
		QueryParameter qp2 = new QueryParameter(vote.getGameKey(), Types.BIGINT);
		QueryParameter qp3 = new QueryParameter(vote.getVoteNumber(), Types.INTEGER);

		return DBConnection.executeUpdateReturnGeneratedKey(LanoelSql.INSERT_VOTE, Arrays.asList(qp1, qp2, qp3));
	}
	
	public Long updateVote(Vote vote) throws Exception
	{
		QueryParameter qp1 = new QueryParameter(vote.getPersonKey(), Types.BIGINT);
		QueryParameter qp2 = new QueryParameter(vote.getGameKey(), Types.BIGINT);
		QueryParameter qp3 = new QueryParameter(vote.getVoteNumber(), Types.INTEGER);
		QueryParameter qp4 = new QueryParameter(vote.getVoteKey(), Types.BIGINT);

		DBConnection.executeUpdateWithParams(LanoelSql.UPDATE_VOTE, Arrays.asList(qp1, qp2, qp3, qp4));

		return vote.getVoteKey();
	}
	
	public List<Vote> getVotesForPerson(Long personKey) throws Exception
	{
		String selectSql = "SELECT * FROM Vote WHERE PersonKey = ?;";
		QueryParameter qp = new QueryParameter(personKey, Types.BIGINT);

		return DBConnection.queryWithParameters(selectSql, Arrays.asList(qp), VoteDatabase::getVoteFromResultSet);
	}
	
	public List<Vote> getVotes() throws Exception
	{
		String voteSelectSql = "SELECT * FROM Vote";
		return DBConnection.queryWithParameters(voteSelectSql, new ArrayList<>(), VoteDatabase::getVoteFromResultSet);
	}

	public static Vote getVoteFromResultSet(ResultSet rs)
	{
		try
		{
			Vote vote = new Vote();
			vote.setPersonKey(rs.getLong("PersonKey"));
			vote.setGameKey(rs.getLong("GameKey"));
			vote.setVoteNumber(rs.getInt("VoteNumber"));
			vote.setVoteKey(rs.getLong("VoteKey"));
			return vote;
		} catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}
}

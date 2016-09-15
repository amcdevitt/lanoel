package computer.lanoel.platform.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.mysql.jdbc.Statement;

import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.database.sql.LanoelSql;

public class VoteDatabase extends DatabaseManager implements IDatabase {

	public VoteDatabase(Connection connection) {
		super(connection);
	}

	public Long insertVote(Vote vote) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.INSERT_VOTE, Statement.RETURN_GENERATED_KEYS);
		
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
	
	public Long updateVote(Vote vote) throws Exception
	{
		PreparedStatement ps = conn.prepareStatement(LanoelSql.UPDATE_VOTE);
		
		int i = 1;
		ps.setLong(i++, vote.getPersonKey());
		ps.setLong(i++, vote.getGameKey());
		ps.setInt(i++, vote.getVoteNumber());
		ps.setLong(i++, vote.getVoteKey());
		ps.executeUpdate();
		
		conn.commit();
		return vote.getVoteKey();
	}
	
	public List<Vote> getVotesForPerson(Long personKey) throws Exception
	{
		List<Vote> voteListToReturn = new ArrayList<Vote>();
		
		if(personKey == null) return voteListToReturn;
		
		String selectSql = "SELECT * FROM Vote WHERE PersonKey = ?;";
		PreparedStatement ps = conn.prepareStatement(selectSql);
		ps.setLong(1, personKey);
		ResultSet rs = ps.executeQuery();

		if(!rs.isBeforeFirst()) return voteListToReturn;
		
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
	
	public Vote getVote(Long voteKey) throws Exception
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

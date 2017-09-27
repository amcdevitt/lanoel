package computer.lanoel.platform.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import computer.lanoel.contracts.Tournaments.Swiss.SwissPlayerRound;
import computer.lanoel.contracts.Tournaments.Swiss.SwissRoundResult;
import computer.lanoel.contracts.Tournaments.Swiss.TournamentSwiss;
import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.*;
import java.util.*;

/**
 * Created by amcde on 3/30/2017.
 */
public class TournamentSwissDatabase extends TournamentDatabase {

    private static Gson _gson;
    public TournamentSwissDatabase() {
        _gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
    }

    public TournamentSwiss createTournament(TournamentSwiss tournamentSwiss) throws SQLException
    {
        tournamentSwiss.type = "SWISS";
        tournamentSwiss.tournamentKey = super.createTournament(tournamentSwiss, "SWISS").tournamentKey;
        return tournamentSwiss;
    }

    public TournamentSwiss updateTournament(TournamentSwiss tournamentSwiss) throws SQLException
    {
        String sql = "UPDATE TournamentSwiss SET " +
                "TournamentData = ?, " +
                "WHERE TournamentSwissKey = ?;";
        QueryParameter qp1 = new QueryParameter(_gson.toJson(tournamentSwiss), Types.OTHER);
        QueryParameter qp2 = new QueryParameter(tournamentSwiss.tournamentSwissKey, Types.BIGINT);
        DBConnection.executeWithParams(sql, Arrays.asList(qp1, qp2));
        return tournamentSwiss;
    }

    public TournamentSwiss getTournament(long tournamentKey) throws SQLException
    {
        String sql = "SELECT * FROM Tournament t " +
                "JOIN TournamentSwiss ts " +
                "ON t.TournamentKey = ts.TournamentKey " +
                "WHERE t.TournamentKey = ?;";

        QueryParameter qp = new QueryParameter(tournamentKey, Types.BIGINT);
        List<TournamentSwiss> tsList = DBConnection.queryWithParameters(sql, Arrays.asList(qp), TournamentSwissDatabase::getTournamentSwissFromResultSet);
        TournamentSwiss ts = tsList.stream().findFirst().get();

        ts.rounds = getSwissRoundList(ts.tournamentKey);
        ts.participants = super.getTournamentParticipantList(ts.tournamentKey);

        return ts;
    }

    public List<SwissPlayerRound> getSwissRoundList(Long tournamentKey) throws SQLException
    {
        String sql = "SELECT * FROM TournamentSwiss_Round WHERE TournamentKey = ?;";
        Set<SwissPlayerRound> sprList = new HashSet<>();

        QueryParameter qp = new QueryParameter(tournamentKey, Types.BIGINT);
        return DBConnection.queryWithParameters(sql, Arrays.asList(qp), TournamentSwissDatabase::getSwissRoundFromResultSet);
    }

    public void insertRoundResult(Long tournamentKey, SwissRoundResult result) throws SQLException
    {
        removeRoundResult(result.playerOneKey, result.roundNumber);
        removeRoundResult(result.playerTwoKey, result.roundNumber);

        String sql = "INSERT INTO TournamentSwiss_Round " +
                "(TournamentKey, RoundData) " +
                "VALUES (?,?);";
        QueryBatch qb = new QueryBatch();
        QueryParameter keyParam = new QueryParameter(tournamentKey, Types.BIGINT);

        for(SwissPlayerRound round : result.getRoundList())
        {
            QueryParameter qp1 = new QueryParameter(_gson.toJson(round), Types.OTHER);
            qb.addBatch(Arrays.asList(keyParam, qp1));
        }

        DBConnection.executeBatch(sql, qb);
    }

    private void removeRoundResult(Long playerKey, int roundNumber) throws SQLException
    {
        String sql = "DELETE FROM TournamentSwiss_Round WHERE PlayerKey = ? AND RoundNumber = ?;";

        QueryParameter qp1 = new QueryParameter(playerKey, Types.BIGINT);
        QueryParameter qp2 = new QueryParameter(roundNumber, Types.BIGINT);
        DBConnection.executeWithParams(sql, Arrays.asList(qp1, qp2));
    }

    public TournamentParticipant addParticipant(Long tournamentKey, TournamentParticipant part) throws SQLException
    {
        return super.addParticipant(tournamentKey, part);
    }

    public void removeParticipantFromTournament(Long part) throws SQLException
    {
        super.removeParticipantFromTournament(part);
    }

    public List<Tournament> getTournamentList()
    {
        try
        {
            return super.getTournamentListByType("SWISS", TournamentSwissDatabase::getTournamentSwissFromResultSet);
        } catch (Exception e)
        {
            return new ArrayList<>();
        }
    }

    public List<Pair<Long, Long>> getCurrentRoundPairings(Long tournamentKey) throws SQLException
    {
        String sql = "SELECT * FROM TournamentSwiss_CurrentRoundPairing WHERE TournamentKey = ?";
        QueryParameter qp = new QueryParameter(tournamentKey, Types.BIGINT);
        return DBConnection.queryWithParameters(sql, Arrays.asList(qp), TournamentSwissDatabase::getRoundPairFromResultSet);
    }

    public void setCurrentRoundPairings(List<Pair<Long, Long>> pairings, Long tournamentKey) throws SQLException
    {
        String sql = "INSERT INTO TournamentSwiss_CurrentRoundPairing " +
                "(TournamentKey, PlayerOneKey, PlayerTwoKey) " +
                "VALUES (?,?,?);";
        QueryParameter keyParam = new QueryParameter(tournamentKey, Types.BIGINT);
        QueryBatch qb = new QueryBatch();
        for(Pair<Long, Long> pair : pairings)
        {
            QueryParameter qp1 = new QueryParameter(pair.getLeft(), Types.BIGINT);
            QueryParameter qp2 = new QueryParameter(pair.getRight(), Types.BIGINT);
            qb.addBatch(Arrays.asList(keyParam, qp1, qp2));
        }
        DBConnection.executeBatch(sql, qb);
    }

    public void removeRoundPairings(Long tournamentKey) throws SQLException {
        String sql = "DELETE FROM TournamentSwiss_CurrentRoundPairing WHERE TournamentKey = ?;";
        QueryParameter qp = new QueryParameter(tournamentKey, Types.BIGINT);
        DBConnection.executeWithParams(sql, Arrays.asList(qp));
    }

    public static TournamentSwiss getTournamentSwissFromResultSet(ResultSet rs)
    {
        try
        {
            TournamentSwiss ts = _gson.fromJson(rs.getString("TournamentData"), TournamentSwiss.class);
            ts.tournamentKey = rs.getLong("TournamentKey");
            return ts;
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static SwissPlayerRound getSwissRoundFromResultSet(ResultSet rs)
    {
        try
        {
            return _gson.fromJson(rs.getString("RoundData"), SwissPlayerRound.class);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Pair<Long, Long> getRoundPairFromResultSet(ResultSet rs)
    {
        try
        {
            Long playerOne, playerTwo;
            playerOne = rs.getLong("PlayerOneKey");
            playerTwo = rs.getLong("PlayerTwoKey");
            return new ImmutablePair<>(playerOne, playerTwo);
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

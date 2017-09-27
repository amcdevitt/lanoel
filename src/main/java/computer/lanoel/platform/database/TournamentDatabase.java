package computer.lanoel.platform.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * Created by amcde on 4/26/2017.
 */
public class TournamentDatabase {

    private static Gson _gson;
    public TournamentDatabase(){
        _gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
    }

    protected TournamentParticipant addParticipant(Long tournamentKey, TournamentParticipant part) throws SQLException
    {
        String sql = "INSERT INTO TournamentParticipant (TournamentKey, TournamentParticipantData)" +
                " VALUES (?,?);";
        QueryParameter qp1 = new QueryParameter(tournamentKey, Types.BIGINT);
        QueryParameter qp2 = new QueryParameter(_gson.toJson(part), Types.OTHER);

        part.tournamentParticipantKey = DBConnection.executeUpdateReturnGeneratedKey(sql, Arrays.asList(qp1, qp2));

        return part;
    }

    protected TournamentParticipant updateParticipant(TournamentParticipant part) throws SQLException
    {
        String sql = "UPDATE TournamentParticipant SET TournamentParticipantData = ? WHERE TournamentParticipantKey = ?;";
        QueryParameter qp1 = new QueryParameter(_gson.toJson(part), Types.OTHER);
        QueryParameter qp2 = new QueryParameter(part.tournamentParticipantKey, Types.BIGINT);

        DBConnection.executeUpdateWithParams(sql, Arrays.asList(qp1, qp2));

        return part;
    }

    protected void removeParticipantFromTournament(Long partKey) throws SQLException
    {
        String sql = "DELETE FROM TournamentParticipant WHERE TournamentParticipantKey = ?;";
        QueryParameter qp = new QueryParameter(partKey, Types.BIGINT);
        DBConnection.executeWithParams(sql, Arrays.asList(qp));
    }

    protected Set<TournamentParticipant> getTournamentParticipantList(long tournamentKey) throws SQLException
    {
        String sql = "SELECT * FROM TournamentParticipant WHERE TournamentKey = ?;";
        QueryParameter qp = new QueryParameter(tournamentKey, Types.BIGINT);
        return new HashSet<>(DBConnection.queryWithParameters(sql, Arrays.asList(qp),
                TournamentDatabase::getTournamentParticipantFromResultSet));
    }

    protected Tournament createTournament(Tournament tournament, String tournamentType) throws SQLException
    {
        String sql = "INSERT INTO Tournament (TournamentType, TournamentData) VALUES (?,?);";
        QueryParameter qp1 = new QueryParameter(tournamentType, Types.VARCHAR);
        QueryParameter qp2 = new QueryParameter(_gson.toJson(tournament), Types.OTHER);
        tournament.tournamentKey = DBConnection.executeUpdateReturnGeneratedKey(sql, Arrays.asList(qp1, qp2));
        return tournament;
    }

    protected Tournament updateTournament(Tournament tournament) throws SQLException
    {
        String sql = "UPDATE Tournament SET TournamentData = ? WHERE TournamentKey = ?;";
        QueryParameter qp1 = new QueryParameter(_gson.toJson(tournament), Types.OTHER);
        QueryParameter qp2 = new QueryParameter(tournament.tournamentKey, Types.BIGINT);
        DBConnection.executeUpdateWithParams(sql, Arrays.asList(qp1, qp2));
        return tournament;
    }

    protected List<Tournament> getTournamentList(Function<ResultSet, Object> resultFunc) throws SQLException
    {
        String sql = "SELECT * FROM Tournament ORDER BY Created DESC";
        List<Tournament> tList = DBConnection.queryWithParameters(sql, new ArrayList<>(), resultFunc);
        for(Tournament tourn : tList)
        {
            tourn.participants = getTournamentParticipantList(tourn.tournamentKey);
        }
        return tList;
    }

    public List<Tournament> getTournamentListByType(String type, Function<ResultSet, Object> resultFunc) throws SQLException
    {
        String sql = "SELECT * FROM Tournament WHERE TournamentType = ? ORDER BY Created DESC";
        QueryParameter qp = new QueryParameter(type, Types.VARCHAR);
        return DBConnection.queryWithParameters(sql, Arrays.asList(qp), resultFunc);
    }

    public static TournamentParticipant getTournamentParticipantFromResultSet(ResultSet rs)
    {
        try
        {
            TournamentParticipant tp = _gson.fromJson(rs.getString("TournamentParticipantData"), TournamentParticipant.class);
            tp.tournamentParticipantKey = rs.getLong("TournamentParticipantKey");
            return tp;
        } catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}

package computer.lanoel.platform.database;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;

import java.sql.*;
import java.util.*;

/**
 * Created by amcde on 4/26/2017.
 */
public class TournamentDatabase extends DatabaseManager {

    private Gson _gson;
    public TournamentDatabase(Connection conn){
        super(conn);
        _gson = new GsonBuilder().setExclusionStrategies(new DatabaseJsonExclusions()).create();
    }

    protected TournamentParticipant addParticipant(Long tournamentKey, TournamentParticipant part) throws SQLException
    {
        String sql = "INSERT INTO TournamentParticipant (TournamentKey, TournamentParticipantData)" +
                " VALUES (?,?);";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, tournamentKey);
        ps.setString(2, _gson.toJson(part));

        ps.execute();

        try(ResultSet keys = ps.getGeneratedKeys())
        {
            if(keys.next())
            {
                part.tournamentParticipantKey = keys.getLong(1);
            }
        }

        return part;
    }

    protected TournamentParticipant updateParticipant(TournamentParticipant part) throws SQLException
    {
        String sql = "UPDATE TournamentParticipant SET TournamentParticipantData = ? WHERE TournamentParticipantKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, _gson.toJson(part));
        ps.setLong(2, part.tournamentParticipantKey);

        ps.executeUpdate();

        return part;
    }

    protected void RemoveParticipantFromTournament(TournamentParticipant part) throws SQLException
    {
        String sql = "DELETE FROM TournamentParticipant WHERE TournamentParticipantKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, part.tournamentParticipantKey);
        ps.execute();
    }

    protected Set<TournamentParticipant> getTournamentParticipantList(long tournamentKey)
    {
        String sql = "SELECT * FROM TournamentParticipant WHERE TournamentKey = ?;";
        Set<TournamentParticipant> tpList = new HashSet<>();

        try(PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, tournamentKey);
            ResultSet rs = ps.executeQuery();

            if(!rs.isBeforeFirst())
            {
                return tpList;
            }

            while(rs.next())
            {
                TournamentParticipant tp = _gson.fromJson(rs.getString("TournamentParticipantData"), TournamentParticipant.class);
                tp.tournamentParticipantKey = rs.getLong("TournamentParticipantKey");
                tpList.add(tp);
            }

            return tpList;

        } catch (SQLException e)
        {
            System.out.println("Exception in getTournamentParticipantList: " + e.getMessage());
            return tpList;
        }
    }

    protected Tournament createTournament(Tournament tournament) throws SQLException
    {
        String sql = "INSERT INTO Tournament (TournamentData) VALUES (?);";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, _gson.toJson(tournament));

        ps.execute();

        try(ResultSet keys = ps.getGeneratedKeys())
        {
            if(keys.next())
            {
                tournament.tournamentKey = keys.getLong(1);
            }
        }

        return tournament;
    }

    protected Tournament updateTournament(Tournament tournament) throws SQLException
    {
        String sql = "UPDATE Tournament SET TournamentData = ? WHERE TournamentKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, _gson.toJson(tournament));
        ps.setLong(2, tournament.tournamentKey);
        ps.executeUpdate();

        return tournament;
    }

    protected List<Tournament> getTournamentList()
    {
        String sql = "SELECT * FROM Tournament ORDER BY Created DESC";
        List<Tournament> tList = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ResultSet rs = ps.executeQuery();

            if(!rs.isBeforeFirst())
            {
                return tList;
            }

            while(rs.next())
            {
                Tournament t = _gson.fromJson(rs.getString("TournamentData"), Tournament.class);
                t.tournamentKey = rs.getLong("TournamentKey");
                t.setCreatedFromSql(rs.getDate("Created"));
                tList.add(t);
            }

            return tList;

        } catch (SQLException e)
        {
            System.out.println("Exception in getTournamentList: " + e);
            return tList;
        }
    }
}

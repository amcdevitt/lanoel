package computer.lanoel.platform.database;

import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;

import java.sql.*;
import java.util.*;

/**
 * Created by amcde on 4/26/2017.
 */
public class TournamentDatabase extends DatabaseManager {

    public TournamentDatabase(Connection conn){super(conn);}

    protected TournamentParticipant addParticipant(Long tournamentKey, TournamentParticipant part) throws SQLException
    {
        String sql = "INSERT INTO TournamentParticipant (TournamentKey, ParticipantName)" +
                " VALUES (?,?);";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setLong(1, tournamentKey);
        ps.setString(2, part.participantName);

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

    protected TournamentParticipant updateParticipantName(TournamentParticipant part) throws SQLException
    {
        String sql = "UPDATE TournamentParticipant SET ParticipantName = ? WHERE TournamentParticipantKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, part.participantName);
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
                TournamentParticipant tp = new TournamentParticipant();
                tp.tournamentParticipantKey = rs.getLong("TournamentParticipantKey");
                tp.participantName = rs.getString("ParticipantName");
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
        String sql = "INSERT INTO Tournament (TournamentName, Type, Created) " +
                "VALUES (?,?,?);";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, tournament.tournamentName);
        ps.setString(2, tournament.type);
        ps.setTimestamp(3, new Timestamp(Calendar.getInstance().getTimeInMillis()));

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

    protected Tournament updateTournamentName(Tournament tournament) throws SQLException
    {
        String sql = "UPDATE Tournament SET TournamentName = ? WHERE TournamentKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, tournament.tournamentName);
        ps.setLong(2, tournament.tournamentKey);
        ps.executeUpdate();

        return tournament;
    }

    protected List<Tournament> getTournamentList(String type)
    {
        String sql = "SELECT * FROM Tournament WHERE Type = ? ORDER BY Created DESC";
        List<Tournament> tList = new ArrayList<>();

        try (PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setString(1, type);
            ResultSet rs = ps.executeQuery();

            if(!rs.isBeforeFirst())
            {
                return tList;
            }

            while(rs.next())
            {
                Tournament t = new Tournament();
                t.tournamentKey = rs.getLong("TournamentKey");
                t.tournamentName = rs.getString("TournamentName");
                t.type = rs.getString("Type");
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

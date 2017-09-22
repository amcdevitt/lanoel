package computer.lanoel.platform.database;

import computer.lanoel.contracts.Tournaments.Swiss.SwissPlayerRound;
import computer.lanoel.contracts.Tournaments.Swiss.SwissRoundResult;
import computer.lanoel.contracts.Tournaments.Swiss.TournamentSwiss;
import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by amcde on 3/30/2017.
 */
public class TournamentSwissDatabase extends TournamentDatabase implements IDatabase {

    public TournamentSwissDatabase(Connection conn) {
        super(conn);
    }

    public TournamentSwiss createTournament(TournamentSwiss tournamentSwiss) throws SQLException
    {
        tournamentSwiss.type = "SWISS";
        tournamentSwiss.tournamentKey = super.createTournament((Tournament)tournamentSwiss).tournamentKey;

        String sql = "INSERT INTO TournamentSwiss " +
                "(TournamentKey, NumberOfRounds, PointsPerGameWon, PointsPerRoundWon, " +
                "PointsForDraw, PlayersCanPlaySamePlayerMoreThanOnce) " +
                "VALUES (?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        int i = 1;
        ps.setLong(i++, tournamentSwiss.tournamentKey);
        ps.setInt(i++, tournamentSwiss.numberOfRounds);
        ps.setInt(i++, tournamentSwiss.pointsPerGameWon);
        ps.setInt(i++, tournamentSwiss.pointsPerRoundWon);
        ps.setInt(i++, tournamentSwiss.pointsPerDraw);
        ps.setBoolean(i++, tournamentSwiss.canPlaySamePlayerMoreThanOnce);

        ps.execute();

        try(ResultSet keys = ps.getGeneratedKeys())
        {
            if(keys.next())
            {
                tournamentSwiss.tournamentSwissKey = keys.getLong(1);
            }
        }

        return tournamentSwiss;
    }

    public TournamentSwiss updateTournament(TournamentSwiss tournamentSwiss) throws SQLException
    {
        String sql = "UPDATE TournamentSwiss SET " +
                "NumberOfRounds = ?, " +
                "PointsPerGameWon = ?, " +
                "PointsPerRoundWon = ?, " +
                "PointsForDraw = ?, " +
                "PlayersCanPlaySamePlayerMoreThanOnce = ? " +
                "WHERE TournamentSwissKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        int i = 1;
        ps.setInt(i++, tournamentSwiss.numberOfRounds);
        ps.setInt(i++, tournamentSwiss.pointsPerGameWon);
        ps.setInt(i++, tournamentSwiss.pointsPerRoundWon);
        ps.setInt(i++, tournamentSwiss.pointsPerDraw);
        ps.setBoolean(i++, tournamentSwiss.canPlaySamePlayerMoreThanOnce);
        ps.executeUpdate();
        return tournamentSwiss;
    }

    public TournamentSwiss getTournament(long tournamentKey)
    {
        String sql = "SELECT * FROM Tournament t " +
                "JOIN TournamentSwiss ts " +
                "ON t.TournamentKey = ts.TournamentKey " +
                "WHERE t.TournamentKey = ?;";
        TournamentSwiss ts = null;
        try(PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, tournamentKey);
            ResultSet rs = ps.executeQuery();

            if(!rs.isBeforeFirst())
            {
                return ts;
            }

            while(rs.next())
            {
                ts = new TournamentSwiss();
                ts.tournamentKey = rs.getLong("TournamentKey");
                ts.tournamentName = rs.getString("TournamentName");
                ts.type = rs.getString("Type");
                ts.setCreatedFromSql(rs.getDate("Created"));
                ts.tournamentSwissKey = rs.getLong("TournamentSwissKey");
                ts.numberOfRounds = rs.getInt("NumberOfRounds");
                ts.pointsPerGameWon = rs.getInt("PointsPerGameWon");
                ts.pointsPerRoundWon = rs.getInt("PointsPerRoundWon");
                ts.pointsPerDraw = rs.getInt("PointsForDraw");
                ts.canPlaySamePlayerMoreThanOnce = rs.getBoolean("PlayersCanPlaySamePlayerMoreThanOnce");
            }

            ts.rounds = getSwissRoundList(ts.tournamentKey);
            ts.participants = super.getTournamentParticipantList(ts.tournamentKey);
            return ts;

        } catch (SQLException e)
        {
            return ts;
        }
    }

    public Set<SwissPlayerRound> getSwissRoundList(Long tournamentKey)
    {
        String sql = "SELECT * FROM TournamentSwiss_Round WHERE TournamentKey = ?;";
        Set<SwissPlayerRound> sprList = new HashSet<>();

        try(PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, tournamentKey);
            ResultSet rs = ps.executeQuery();

            if(!rs.isBeforeFirst())
            {
                return sprList;
            }

            while(rs.next())
            {
                SwissPlayerRound spr = new SwissPlayerRound();
                spr.roundNumber = rs.getInt("RoundNumber");
                spr.playerKey = rs.getLong("PlayerKey");
                spr.gamesWon = rs.getInt("GamesWon");
                spr.draws = rs.getInt("Draws");
                spr.drop = rs.getBoolean("PlayerDrop");
                spr.roundWon = rs.getBoolean("roundWon");
                sprList.add(spr);
            }

            return sprList;
        } catch (SQLException e)
        {
            System.out.println("Exception in getSwissRoundList: " + e.getMessage());
            return sprList;
        }
    }

    public void insertRoundResult(Long tournamentKey, SwissRoundResult result) throws SQLException
    {
        removeRoundResult(result.playerOneKey, result.roundNumber);
        removeRoundResult(result.playerTwoKey, result.roundNumber);

        String sql = "INSERT INTO TournamentSwiss_Round " +
                "(TournamentKey, RoundNumber, PlayerKey, GamesWon, Draws, PlayerDrop, RoundWon) " +
                "VALUES (?,?,?,?,?,?,?);";
        PreparedStatement ps = conn.prepareStatement(sql);

        int i = 1;
        ps.setLong(i++, tournamentKey);
        ps.setInt(i++, result.roundNumber);
        ps.setLong(i++, result.playerOneKey);
        ps.setInt(i++, result.gamesWonPlayerOne);
        ps.setInt(i++, result.draws);
        ps.setBoolean(i++, result.playerOneDrop);
        ps.setBoolean(i++, result.gamesWonPlayerOne > result.gamesWonPlayerTwo);
        ps.addBatch();

        i = 1;
        ps.setLong(i++, tournamentKey);
        ps.setInt(i++, result.roundNumber);
        ps.setLong(i++, result.playerTwoKey);
        ps.setInt(i++, result.gamesWonPlayerTwo);
        ps.setInt(i++, result.draws);
        ps.setBoolean(i++, result.playerTwoDrop);
        ps.setBoolean(i++, result.gamesWonPlayerTwo > result.gamesWonPlayerOne);
        ps.addBatch();

        ps.executeBatch();
    }

    private void removeRoundResult(Long playerKey, int roundNumber)
    {
        String sql = "DELETE FROM TournamentSwiss_Round WHERE PlayerKey = ? AND RoundNumber = ?;";
        try(PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setLong(1, playerKey);
            ps.setInt(2, roundNumber);
            ps.execute();
            conn.commit();
        } catch (SQLException e)
        {

        }
    }

    public TournamentParticipant addParticipant(Long tournamentKey, TournamentParticipant part) throws SQLException
    {
        return super.addParticipant(tournamentKey, part);
    }

    public void removeParticipantFromTournament(TournamentParticipant part) throws SQLException
    {
        super.RemoveParticipantFromTournament(part);
    }

    public TournamentSwiss updateTournamentName(Tournament tournament) throws SQLException
    {
        super.updateTournament(tournament);
        return getTournament(tournament.tournamentKey);
    }

    public List<Tournament> getTournamentList()
    {
        return super.getTournamentList().stream().filter(t -> t.type.equalsIgnoreCase("swiss")).collect(Collectors.toList());
    }

    public List<Pair<Long, Long>> getCurrentRoundPairings(Long tournamentKey) throws SQLException
    {
        String sql = "SELECT * FROM TournamentSwiss_CurrentRoundPairing WHERE TournamentKey = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, tournamentKey);
        ResultSet rs = ps.executeQuery();

        List<Pair<Long, Long>> pairings = new ArrayList<>();

        if(!rs.isBeforeFirst())
        {
            return new ArrayList<>();
        }

        while(rs.next())
        {
            Long playerOne, playerTwo;
            playerOne = rs.getLong("PlayerOneKey");
            playerTwo = rs.getLong("PlayerTwoKey");
            pairings.add(new ImmutablePair<>(playerOne, playerTwo));
        }
        return pairings;
    }

    public void setCurrentRoundPairings(List<Pair<Long, Long>> pairings, Long tournamentKey) throws SQLException
    {
        String sql = "INSERT INTO TournamentSwiss_CurrentRoundPairing " +
                "(TournamentKey, PlayerOneKey, PlayerTwoKey) " +
                "VALUES (?,?,?);";
        PreparedStatement ps = conn.prepareStatement(sql);

        for(Pair<Long, Long> pair : pairings)
        {
            Long rightPlayer = pair.getRight();
            ps.setLong(1, tournamentKey);
            ps.setLong(2, pair.getLeft());
            if(rightPlayer == null)
            {
                ps.setNull(3, Types.BIGINT);
            } else {
                ps.setLong(3, rightPlayer);
            }
            ps.addBatch();
        }
        ps.executeBatch();
    }

    public void removeRoundPairings(Long tournamentKey) throws SQLException {
        String sql = "DELETE FROM TournamentSwiss_CurrentRoundPairing WHERE TournamentKey = ?;";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setLong(1, tournamentKey);
        ps.execute();
    }
}

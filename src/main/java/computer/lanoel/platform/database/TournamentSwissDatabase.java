package computer.lanoel.platform.database;

import computer.lanoel.contracts.Tournaments.Swiss.SwissRoundResult;
import computer.lanoel.contracts.Tournaments.Swiss.TournamentSwiss;
import computer.lanoel.contracts.Tournaments.Tournament;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by amcde on 3/30/2017.
 */
public class TournamentSwissDatabase extends TournamentDatabase implements IDatabase {

    public TournamentSwissDatabase(Connection conn) {
        super(conn);
    }

    public TournamentSwiss createTournament(TournamentSwiss tournament) throws SQLException
    {
        return new TournamentSwiss();
    }

    public TournamentSwiss updateTournament(TournamentSwiss tournament) throws SQLException
    {
        return new TournamentSwiss();
    }

    public TournamentSwiss getTournament(long tournamentKey)
    {
        return new TournamentSwiss();
    }

    public TournamentSwiss updateRound(SwissRoundResult result) throws SQLException
    {
        return new TournamentSwiss();
    }

}

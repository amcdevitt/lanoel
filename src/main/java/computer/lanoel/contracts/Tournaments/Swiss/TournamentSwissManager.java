package computer.lanoel.contracts.Tournaments.Swiss;

import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.IDatabase;
import computer.lanoel.platform.database.TournamentSwissDatabase;

/**
 * Created by amcde on 4/20/2017.
 */
public class TournamentSwissManager {

    private TournamentSwissDatabase db;
    private TournamentSwiss tournament;

    public TournamentSwissManager(long tournamentKey) throws Exception
    {
        db = (TournamentSwissDatabase) DatabaseFactory.getInstance().getDatabase("TOURNAMENT_SWISS");
        tournament = db.getTournament(tournamentKey);
    }

    public static TournamentSwiss createTournament() throws Exception
    {
        TournamentSwissDatabase database = (TournamentSwissDatabase) DatabaseFactory.getInstance().getDatabase("TOURNAMENT_SWISS");
        TournamentSwiss tournament = new TournamentSwiss();
        tournament.canPlaySamePlayerMoreThanOnce = false;
        tournament.numberOfRounds = 1;
        tournament.pointsPerGameWon = 3;
        tournament.pointsPerDraw = 1;
        tournament.pointsPerRoundWon = 0;

        return database.createTournament(tournament);
    }

    public TournamentSwiss recordResult(SwissRoundResult result) throws Exception
    {
        return db.updateRound(result);
    }

}

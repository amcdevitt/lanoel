package computer.lanoel.contracts.Tournaments.Swiss;

import computer.lanoel.contracts.Tournaments.Tournament;

import java.util.Set;

/**
 * Created by amcde on 3/30/2017.
 */
public class TournamentSwiss extends Tournament {

    public Long tournamentSwissKey;
    public Integer numberOfRounds;
    public Integer pointsPerGameWon;
    public Integer pointsPerRoundWon;
    public Integer pointsPerDraw;
    public boolean canPlaySamePlayerMoreThanOnce;

    public Set<SwissPlayerRound> rounds;

}

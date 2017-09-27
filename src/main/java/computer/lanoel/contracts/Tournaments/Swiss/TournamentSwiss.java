package computer.lanoel.contracts.Tournaments.Swiss;

import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
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

    public List<SwissPlayerRound> rounds;
    public List<Pair<Long, Long>> currentRoundPairings;

    public List<Pair<Long, Long>> getCurrentRoundPairings() {
        return currentRoundPairings;
    }

    public void setCurrentRoundPairings(List<Pair<Long, Long>> currentRoundPairings) {
        this.currentRoundPairings = currentRoundPairings;
    }

    public Map<String, Integer> score;
}

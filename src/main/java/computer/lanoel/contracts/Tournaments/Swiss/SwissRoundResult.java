package computer.lanoel.contracts.Tournaments.Swiss;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by amcde on 4/20/2017.
 */
public class SwissRoundResult {

    public Integer roundNumber;
    public Long playerOneKey;
    public Long playerTwoKey;
    public Integer gamesWonPlayerOne;
    public Integer gamesWonPlayerTwo;
    public Integer draws;
    public boolean playerOneDrop;
    public boolean playerTwoDrop;

    public List<SwissPlayerRound> getRoundList()
    {
        SwissPlayerRound r1 = new SwissPlayerRound();
        SwissPlayerRound r2 = new SwissPlayerRound();

        r1.roundNumber = this.roundNumber;
        r1.playerKey = this.playerOneKey;
        r1.gamesWon = this.gamesWonPlayerOne;
        r1.drop = this.playerOneDrop;
        r1.draws = this.draws;
        r1.roundWon = this.gamesWonPlayerOne > this.gamesWonPlayerTwo;

        r2.roundNumber = this.roundNumber;
        r2.playerKey = this.playerTwoKey;
        r2.gamesWon = this.gamesWonPlayerTwo;
        r2.drop = this.playerTwoDrop;
        r2.draws = this.draws;
        r2.roundWon = !r1.roundWon;

        return Arrays.asList(r1, r2);
    }
}

package computer.lanoel.platform.database;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Tournaments.Lanoel.TournamentLanoel;
import computer.lanoel.contracts.Tournaments.Tournament;

/**
 * Created by amcde on 9/15/2017.
 */
public class DatabaseJsonExclusions implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == Person.class && f.getName().equalsIgnoreCase("steamInfo"))||
                (f.getDeclaringClass() == Person.class && f.getName().equalsIgnoreCase("GameVote1"))||
                (f.getDeclaringClass() == Person.class && f.getName().equalsIgnoreCase("GameVote2"))||
                (f.getDeclaringClass() == Person.class && f.getName().equalsIgnoreCase("GameVote3")) ||
                (f.getDeclaringClass() == Tournament.class && f.getName().equalsIgnoreCase("scores")) ||
                (f.getDeclaringClass() == Tournament.class && f.getName().equalsIgnoreCase("TournamentKey")) ||
                (f.getDeclaringClass() == TournamentLanoel.class && f.getName().equalsIgnoreCase("TournamentKey")) ||
                (f.getDeclaringClass() == Tournament.class && f.getName().equalsIgnoreCase("TournamentName")) ||
                (f.getDeclaringClass() == TournamentLanoel.class && f.getName().equalsIgnoreCase("TournamentName")) ||
                (f.getDeclaringClass() == Game.class && f.getName().equalsIgnoreCase("GameKey"));
    }
}

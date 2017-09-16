package computer.lanoel.platform.database;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import computer.lanoel.contracts.Person;

/**
 * Created by amcde on 9/15/2017.
 */
public class DatabaseJsonExclusions implements ExclusionStrategy {

    public boolean shouldSkipClass(Class<?> arg0) {
        return false;
    }

    public boolean shouldSkipField(FieldAttributes f) {

        return (f.getDeclaringClass() == Person.class && f.getName().equals("steamInfo"))||
                (f.getDeclaringClass() == Person.class && f.getName().equals("GameVote1"))||
                (f.getDeclaringClass() == Person.class && f.getName().equals("GameVote2"))||
                (f.getDeclaringClass() == Person.class && f.getName().equals("GameVote3"));
    }
}

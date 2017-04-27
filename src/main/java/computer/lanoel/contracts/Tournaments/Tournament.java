package computer.lanoel.contracts.Tournaments;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Set;

/**
 * Created by amcde on 3/30/2017.
 */
public class Tournament {

    public Long tournamentKey;
    public String tournamentName;
    public String type;
    private Calendar created;
    public Set<TournamentParticipant> participants;

    public Tournament()
    {
        created = Calendar.getInstance();
    }

    public Calendar getCreated() {
        return created;
    }

    public void setCreated(Calendar created) {
        this.created = created;
    }

    public void setCreatedFromSql(Date date)
    {
        this.created = new GregorianCalendar();
        this.created.setTime(date);
    }
}

package computer.lanoel.contracts;

import com.fasterxml.jackson.annotation.JsonIgnore;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import computer.lanoel.platform.database.TournamentLanoelDatabase;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Place
{
	private int place;
	@JsonIgnore
	private Long roundKey;
	private TournamentParticipant participant;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + place;
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Place other = (Place) obj;
		if (place != other.place)
			return false;
		return true;
	}

	public int getPlace()
	{
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}

	public Long getRoundKey()
	{
		return roundKey;
	}

	public void setRoundKey(Long roundKey)
	{
		this.roundKey = roundKey;
	}

	public TournamentParticipant getParticipant() {
		return participant;
	}

	public void setParticipant(TournamentParticipant participant) {
		this.participant = participant;
	}
}

package computer.lanoel.contracts.Tournaments.Lanoel;


import computer.lanoel.contracts.Tournaments.TournamentParticipant;

public class Score
{

	public String personName;
	public int score;
	public TournamentParticipant participant;
	
	public Score()
	{

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((personName == null) ? 0 : personName.hashCode());
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
		Score other = (Score) obj;
		if (personName == null)
		{
			if (other.personName != null)
				return false;
		} else if (!personName.equals(other.personName))
			return false;
		return true;
	}
}

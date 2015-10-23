package computer.lanoel.contracts;

public class Game implements Comparable<Game>
{
	private Long GameKey;
	private String GameName;
	private String Location;
	private String Rules;
	private int VoteTotal;
	private int uniquePersonVotes;
	
	public Long getGameKey()
	{
		return GameKey;
	}

	public void setGameKey(Long gameKey)
	{
		GameKey = gameKey;
	}

	public String getGameName()
	{
		return GameName;
	}
	
	public void setGameName(String gameName)
	{
		GameName = gameName;
	}
	
	public String getLocation()
	{
		return Location;
	}
	
	public void setLocation(String location)
	{
		Location = location;
	}
	
	public String getRules()
	{
		return Rules;
	}
	
	public void setRules(String rules)
	{
		Rules = rules;
	}

	public int getVoteTotal()
	{
		return VoteTotal;
	}

	public void setVoteTotal(int voteTotal)
	{
		VoteTotal = voteTotal;
	}

	/**
	 * @return the numUniquePersonVotes
	 */
	public int getNumUniquePersonVotes() {
		return uniquePersonVotes;
	}

	/**
	 * @param numUniquePersonVotes the numUniquePersonVotes to set
	 */
	public void setNumUniquePersonVotes(int numUniquePersonVotes) {
		this.uniquePersonVotes = numUniquePersonVotes;
	}
	
	@Override
	public int compareTo(Game g) {
		// TODO Auto-generated method stub
		Integer myVoteTotal = this.VoteTotal;
		Integer gameVoteTotal = g.VoteTotal;
		
		// Intentionally backwards to get list in descending order
		int voteComparison = gameVoteTotal.compareTo(myVoteTotal);
		
		if(voteComparison == 0)
		{
			Integer myUniquePersonVotes = this.uniquePersonVotes;
			Integer gameUniquePersonVotes = g.uniquePersonVotes;
			
			// Intentionally backwards to get list in descending order
			return gameUniquePersonVotes.compareTo(myUniquePersonVotes);
		}

		return voteComparison;
	}

}

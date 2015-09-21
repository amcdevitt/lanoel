package computer.lanoel.contracts;

public class Game
{
	private Long GameKey;
	private String GameName;
	private String Location;
	private String Rules;
	private int VoteTotal;
	
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

}

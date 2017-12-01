package computer.lanoel.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Vote
{
	private Long VoteKey;
	private Long GameKey;
	private Long PersonKey;
	private int VoteNumber;
	
	public Long getVoteKey()
	{
		return VoteKey;
	}
	
	public void setVoteKey(Long voteKey)
	{
		VoteKey = voteKey;
	}

	public int getVoteNumber()
	{
		return VoteNumber;
	}

	public void setVoteNumber(int voteNumber)
	{
		VoteNumber = voteNumber;
	}

	public Long getGameKey()
	{
		return GameKey;
	}

	public void setGameKey(Long gameKey)
	{
		GameKey = gameKey;
	}

	public Long getPersonKey()
	{
		return PersonKey;
	}

	public void setPersonKey(Long personKey)
	{
		PersonKey = personKey;
	}
	
}

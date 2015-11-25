package computer.lanoel.contracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament
{

	private Long tournamentKey;
	private String tournamentName;
	private List<Round> roundList;
	
	public Tournament()
	{
		roundList = new ArrayList<Round>();
	}
	
	public Long getTournamentKey()
	{
		return tournamentKey;
	}
	
	public void setTournamentKey(Long tournamentKey)
	{
		this.tournamentKey = tournamentKey;
	}
	
	public String getTournamentName()
	{
		return tournamentName;
	}
	
	public void setTournamentName(String tournamentName)
	{
		this.tournamentName = tournamentName;
	}

	public List<Round> getRounds()
	{
		return roundList;
	}

	public void setRounds(List<Round> rounds)
	{
		this.roundList = rounds;
	}
	
	public void addRound(Round round)
	{
		if(roundList.contains(round))
		{
			roundList.remove(roundList.indexOf(round));
		}
		
		roundList.add(round);
	}
}

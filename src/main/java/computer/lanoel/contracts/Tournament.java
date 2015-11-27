package computer.lanoel.contracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import computer.lanoel.platform.ServiceUtils;

public class Tournament
{

	private Long tournamentKey;
	private String tournamentName;
	private List<Round> roundList;
	@JsonUnwrapped
	private Map<String, Integer> scores;
	@JsonIgnore
	private Map<Integer, Integer> pointValues;
	
	public Tournament()
	{
		scores = new HashMap<String, Integer>();
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
	
	public void populateScore() throws Exception
	{
		populatePointValues();
		
		for(Round round: roundList)
		{
			if(round.getPlaces() == null) continue;
			Iterator it = round.getPlaces().entrySet().iterator();
			
			while(it.hasNext())
			{
				Map.Entry<Integer, String> pair = (Map.Entry)it.next();
				String personName = pair.getValue();
				int place = pair.getKey();
				//it.remove();
				
				if(!scores.containsKey(personName))
				{
					scores.put(personName, 0);
				}
				
				int currentScore = scores.get(personName);
				scores.put(personName, currentScore + pointValues.get(place));
			}
		}
	}
	
	public Map<String, Integer> getScores() throws Exception
	{		
		return scores;
	}
	
	private void populatePointValues() throws Exception
	{
		pointValues = ServiceUtils.storage().getPointValues();
	}
}

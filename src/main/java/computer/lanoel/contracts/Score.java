package computer.lanoel.contracts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import computer.lanoel.platform.ServiceUtils;

public class Score
{

	private String tournamentName;
	private List<Round> rounds;
	@JsonUnwrapped
	private Map<String, Integer> scores;
	@JsonIgnore
	private Map<Integer, Integer> pointValues;
	
	public Score()
	{
		scores = new HashMap<String, Integer>();
		rounds = new ArrayList<Round>();
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
		return rounds;
	}

	public void setRounds(List<Round> rounds)
	{
		this.rounds = rounds;
	}
	
	public void addRound(Round round)
	{
		rounds.add(round);
	}
	
	public void populateScore() throws Exception
	{
		populatePointValues();
		
		for(Round round: rounds)
		{
			if(round.getPlaces() == null) continue;
			Iterator it = round.getPlaces().entrySet().iterator();
			
			while(it.hasNext())
			{
				Map.Entry<Integer, String> pair = (Map.Entry)it.next();
				String personName = pair.getValue();
				int place = pair.getKey();
				it.remove();
				
				if(!scores.containsKey(personName))
				{
					scores.put(personName, 0);
				}
				
				int currentScore = scores.get(personName);
				scores.put(personName, currentScore + pointValues.get(place));
			}
		}
	}
	
	@JsonAnyGetter
	public Map<String, Integer> getScores() throws Exception
	{		
		return scores;
	}
	
	private void populatePointValues() throws Exception
	{
		pointValues = ServiceUtils.storage().getPointValues();
	}
}

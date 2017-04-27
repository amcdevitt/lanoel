package computer.lanoel.contracts.Tournaments.Lanoel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Round;
import computer.lanoel.contracts.Score;

public class TournamentLanoel
{

	private Long tournamentKey;
	private String tournamentName;
	private List<Round> roundList;
	private List<Score> scores;
	@JsonIgnore
	private Map<Integer, Integer> pointValues;
	
	public TournamentLanoel()
	{
		scores = new ArrayList<Score>();
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
		for(Round round: roundList)
		{
			if(round.getPlaces() == null || round.getPlaces().size() == 0) continue;
			
			for(Place placeObj : round.getPlaces())
			{
				String personName = placeObj.getPerson();
				
				Score playerScore = new Score();
				playerScore.personName = personName;
				playerScore.score = placeObj.getPointValue();
				
				if(scores.contains(playerScore))
				{
					Score tempScore = scores.get(scores.indexOf(playerScore));
					tempScore.score += playerScore.score;
				}
				else
				{
					scores.add(playerScore);
				}
			}
		}
	}
	
	public List<Score> getScores() throws Exception
	{		
		return scores;
	}
}

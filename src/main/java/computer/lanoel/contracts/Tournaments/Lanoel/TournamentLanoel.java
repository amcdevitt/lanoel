package computer.lanoel.contracts.Tournaments.Lanoel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Tournaments.Tournament;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TournamentLanoel extends Tournament
{
	private List<Round> roundList;
	private List<Score> scores;
	private Map<Integer, Integer> pointValues;
	
	public TournamentLanoel()
	{
		scores = new ArrayList<Score>();
		roundList = new ArrayList<Round>();
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
	
	public List<Score> getScores() throws Exception
	{
		for(Round round: roundList)
		{
			if(round.getPlaces() == null || round.getPlaces().size() == 0) continue;

			for(Place placeObj : round.getPlaces())
			{
				String personName = placeObj.getParticipant().participantName;

				Score playerScore = new Score();
				playerScore.personName = personName;
				int numberOfPlaces = round.getPlaces().size();
				int place = placeObj.getPlace() == 99 ? numberOfPlaces : placeObj.getPlace();
				playerScore.score = numberOfPlaces - place;

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

		return scores;
	}
}

package computer.lanoel.contracts.Tournaments.Lanoel;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Place;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Round implements Comparable<Round>
{
	private Long roundKey;
	private int roundNumber;
	@JsonUnwrapped
	private List<Place> places;
	private Game game;
	
	public Round()
	{
		places = new ArrayList<Place>();
	}

	public Long getRoundKey()
	{
		return roundKey;
	}

	public void setRoundKey(Long roundKey)
	{
		this.roundKey = roundKey;
	}

	public List<Place> getPlaces()
	{
		return places;
	}

	public void setPlaces(List<Place> places)
	{
		this.places = places;
	}

	public int getRoundNumber()
	{
		return roundNumber;
	}

	public void setRoundNumber(int roundNumber)
	{
		this.roundNumber = roundNumber;
	}

	public Game getGame()
	{
		return game;
	}

	public void setGame(Game game)
	{
		this.game = game;
	}
	
	@Override
	public int compareTo(Round other)
	{
		Integer thisNumber = this.roundNumber;
		Integer otherNumber = other.roundNumber;
		
		return thisNumber.compareTo(otherNumber);
	}
	
	@Override
	public boolean equals(Object other)
	{
		return this.roundNumber == ((Round)other).roundNumber;
	}
}

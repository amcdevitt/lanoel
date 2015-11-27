package computer.lanoel.contracts;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Round implements Comparable<Round>
{
	private Long roundKey;
	private int roundNumber;
	@JsonUnwrapped
	private Map<Integer, String> places;
	private Game game;
	
	public Round()
	{
		places = new HashMap<Integer, String>();
	}

	public Long getRoundKey()
	{
		return roundKey;
	}

	public void setRoundKey(Long roundKey)
	{
		this.roundKey = roundKey;
	}

	public Map<Integer, String> getPlaces()
	{
		return places;
	}

	public void setPlaces(Map<Integer, String> place)
	{
		this.places = place;
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

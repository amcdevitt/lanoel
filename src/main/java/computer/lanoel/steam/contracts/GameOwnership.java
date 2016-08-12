package computer.lanoel.steam.contracts;

import java.util.HashSet;
import java.util.Set;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;

public class GameOwnership {

	public Game game;
	public Set<Person> owners;
	public Set<Person> nonOwners;
	
	public GameOwnership()
	{
		owners = new HashSet<Person>();
		nonOwners = new HashSet<Person>();
	}
}

package computer.lanoel.platform;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import computer.lanoel.contracts.Person;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.steam.contracts.PlayerSteamInformation;

public class InitialPersonInfo {

	public static Set<Person> personSet()
	{
		Set<Person> personSet = new HashSet<Person>();
		
		personSet.add(new Person("amcdevitt@gmail.com",new PlayerSteamInformation(76561198025924899L)));
		personSet.add(new Person("bingemi@gmail.com",new PlayerSteamInformation(76561197996395908L)));
		personSet.add(new Person("jon.brunette@gmail.com",new PlayerSteamInformation(76561198013395722L)));
		personSet.add(new Person("patdomalley@gmail.com",new PlayerSteamInformation(76561197987064045L)));
		personSet.add(new Person("dinosauricfury@gmail.com",new PlayerSteamInformation(76561198040727031L)));
		personSet.add(new Person("donkeyfire@gmail.com",new PlayerSteamInformation(76561197970569991L)));
		personSet.add(new Person("joetheshow666@gmail.com",new PlayerSteamInformation(76561198006484066L)));
		personSet.add(new Person("rykosow@gmail.com",new PlayerSteamInformation(76561197993346479L)));
		personSet.add(new Person("tgorin@gmail.com",new PlayerSteamInformation(76561197972784493L)));
		personSet.add(new Person("nickfro@gmail.com",new PlayerSteamInformation(76561197972979911L)));
		personSet.add(new Person("idioSyncr4zy@gmail.com",new PlayerSteamInformation(76561197987038385L)));
		personSet.add(new Person("mikeshaw@gmail.com",new PlayerSteamInformation(76561197993346349L)));
		
		return personSet;		
	}
	
	public static void initializePlayerDb() throws Exception
	{
		PersonDatabase db = (PersonDatabase)DatabaseFactory.getInstance().getDatabase("GAME");
		List<Person> personList = db.getPersonList();
		Set<String> personListUserNames = personList.stream().map(p -> p.getUserName()).collect(Collectors.toSet());
		
		for(Person person : personSet())
		{
			if(!personListUserNames.contains(person.getUserName()))
			{
				db.insertPerson(person);
			}
		}
	}
}

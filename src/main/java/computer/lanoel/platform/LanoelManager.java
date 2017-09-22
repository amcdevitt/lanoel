package computer.lanoel.platform;

import java.util.List;
import java.util.Map;

import computer.lanoel.communication.LANoelAuth;
import computer.lanoel.communication.User;
import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Tournaments.Lanoel.Round;
import computer.lanoel.contracts.Tournaments.Lanoel.TournamentLanoel;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.GameDatabase;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.platform.database.TournamentLanoelDatabase;

public class LanoelManager {

	private UserAccount _user;
	
	public LanoelManager(User user) throws Exception
	{
		getLoggedInUser(user);
		
		if(!LANoelAuth.isAdminUser(_user))
		{
			throw new InvalidSessionException("User is not an admin user", user.getSessionId());
		}
	}
	
	private void getLoggedInUser(User user) throws Exception
	{
		UserAccount uAcct = LANoelAuth.loggedInUser(user.getSessionId());
		
		if(uAcct == null)
		{
			throw new InvalidSessionException("User not logged in!", user.getSessionId());
		}
		_user = uAcct;		
	}
	
	public String getSessionIdForUser()
	{
		return _user.getUser().getSessionId();
	}
	
	public Long createTournament(String tournamentName) throws Exception
	{
		TournamentLanoel tourn = new TournamentLanoel();
    	tourn.setTournamentName(tournamentName);
    	TournamentLanoelDatabase db = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
    	return db.insertTournament(tourn);
	}
	
	public void createRound(Round round, Long tournamentKey) throws Exception
	{
		TournamentLanoelDatabase db = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
		GameDatabase gameDb = (GameDatabase)DatabaseFactory.getInstance().getDatabase("GAME");
		
		if(round.getGame() == null)
    	{
    		throw new BadRequestException("Please provide a game");
    	}
    	
    	if(gameDb.getGame(round.getGame().getGameKey()) == null)
    	{
    		throw new BadRequestException("Game does not exist");
    	}
    	
    	if(round.getRoundNumber() <= 0)
    	{
    		throw new BadRequestException("Please provide a valid round number");
    	}
    	
    	List<Round> existingRounds = db.getRounds(tournamentKey);
    	if(existingRounds.contains(round))
    	{
    		db.updateRound(tournamentKey, round);
    	}
    	else
    	{
    		Long roundKey = db.insertRound(tournamentKey, round);
			initializeRound(roundKey);
    	}
	}

	private void initializeRound(Long roundKey) throws Exception
	{
		PersonDatabase pDb = (PersonDatabase)DatabaseFactory.getInstance().getDatabase("PERSON");
		TournamentLanoelDatabase tDb = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
		List<Person> personList = pDb.getPersonList();

		int i = 1;
		for(Person person : personList)
		{
			tDb.insertRoundStanding(person.getPersonKey(), roundKey, i++);
		}
	}
	
	public void recordResult(Long tournamentKey, Long personKey, int roundNumber, int place) throws Exception
	{   
		TournamentLanoelDatabase db = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
    	List<Round> roundList = db.getRounds(tournamentKey);
    	Round tempRound = new Round();
    	tempRound.setRoundNumber(roundNumber);
    	Round round = null;
    	
    	try
    	{
    		round = roundList.get(roundList.indexOf(tempRound));
    	} catch (Exception e)
    	{
    		throw new Exception("Round " + roundNumber + " does not exist");
    	}
    		
    	db.insertRoundStanding(personKey, round.getRoundKey(), place);
	}
	
	public void updateScores(Long tournamentKey, int roundNumber, List<Place> places) throws Exception
	{
		TournamentLanoelDatabase db = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
    	List<Round> roundList = db.getTournament(tournamentKey).getRounds();
    	Round tempRound = new Round();
    	tempRound.setRoundNumber(roundNumber);
    	Round round = null;
    	
    	try
    	{
    		round = roundList.get(roundList.indexOf(tempRound));
    	} catch (Exception e)
    	{
    		throw new BadRequestException("Round " + roundNumber + " does not exist");
    	}

		db.replaceRoundStandings(round.getRoundKey(), places);
	}
	
	public void resetRoundScores(Long tournamentKey, int roundNumber) throws Exception
	{
		TournamentLanoelDatabase db = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
		List<Round> roundList = db.getTournament(tournamentKey).getRounds();
    	Round tempRound = new Round();
    	tempRound.setRoundNumber(roundNumber);
    	Round round = null;
    	
    	try
    	{
    		round = roundList.get(roundList.indexOf(tempRound));
    	} catch (Exception e)
    	{
    		throw new BadRequestException("Round " + roundNumber + " does not exist");
    	}

		db.resetRoundStandings(round.getRoundKey());
	}
	
	public void setPointValues(Map<Integer, Integer> pointMap) throws Exception
	{
		TournamentLanoelDatabase db = (TournamentLanoelDatabase)DatabaseFactory.getInstance().getDatabase("TOURNAMENT");
		db.updatePointValues(pointMap);
	}
}

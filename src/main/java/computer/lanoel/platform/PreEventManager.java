package computer.lanoel.platform;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import computer.lanoel.communication.HttpHelper;
import computer.lanoel.communication.LANoelAuth;
import computer.lanoel.communication.User;
import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Vote;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.GameDatabase;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.platform.database.VoteDatabase;
import computer.lanoel.steam.SteamCache;
import computer.lanoel.steam.contracts.GameOwnership;
import computer.lanoel.steam.contracts.SteamGame;

public class PreEventManager {

	private UserAccount _user;
	private Calendar _voteCutoffTime;
	
	private static final String VOTE_CUTOFF_TIME = "2016-11-01T00:00"; // yyyy-MM-dd'T'HH:mm
	
	public PreEventManager(User user) throws Exception
	{
		try
		{
			if(user != null && user.getSessionId() != null)
			{
				_user = LANoelAuth.loggedInUser(user.getSessionId());
			}
		} catch (Exception e)
		{
			// Some methods do not require a logged in user
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
		_voteCutoffTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
		_voteCutoffTime.setTime(sdf.parse(VOTE_CUTOFF_TIME));
	}
	
	public String getSessionIdForUser()
	{
		return _user == null ? null : _user.getUser().getSessionId();
	}
	
	public void vote(Vote vote, Long personKey) throws Exception
	{
		if(_user == null)
		{
			throw new InvalidSessionException("User not logged in!", _user.getUser().getSessionId());
		}
		
		if(SteamCache.instance().getUserFromUserAccount(_user).getPersonKey() != personKey)
		{
			throw new BadRequestException("Cannot vote on behalf of another player!");
		}
		
		if(vote.getGameKey() == null) throw new BadRequestException("no game provided");
    	if(personKey == null) throw new BadRequestException("no person provided");
    	
    	vote.setPersonKey(personKey);
    	
    	VoteDatabase voteDb = (VoteDatabase)DatabaseFactory.getInstance().getDatabase("VOTE");
    	GameDatabase gameDb = (GameDatabase)DatabaseFactory.getInstance().getDatabase("GAME");
    	PersonDatabase personDb = (PersonDatabase)DatabaseFactory.getInstance().getDatabase("PERSON");
    	
    	if(gameDb.getGame(vote.getGameKey()) == null)
    	{
    		throw new BadRequestException("Game does not exist");
    	}
    	
    	if(personDb.getPerson(vote.getPersonKey()) == null)
    	{
    		throw new BadRequestException("Person does not exist");
    	}
    	
    	if(vote.getVoteNumber() < 1 || vote.getVoteNumber() > 3)
    	{
    		throw new BadRequestException("Votes must be 1, 2, or 3");
    	}
    	
    	Calendar currentTime = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));
    	if(currentTime.after(_voteCutoffTime))
    	{
    		throw new BadRequestException("Voting ended!");
    	}
    	
    	List<Vote> votesForPerson = voteDb.getVotesForPerson(vote.getPersonKey());
    	if(votesForPerson == null || votesForPerson.isEmpty())
    	{
    		voteDb.insertVote(vote);
    		return;
    	}
    	else
    	{
    		for(Vote recordedVote : votesForPerson)
    		{
    			if(vote.getVoteNumber() == recordedVote.getVoteNumber())
    			{
    				vote.setVoteKey(recordedVote.getVoteKey());
    				voteDb.updateVote(vote);
    				return;
    			}
    		}    		
    		voteDb.insertVote(vote);
    	}
	}
	
	public Set<Game> getGameList()
	{
		return SteamCache.instance().getGames();
	}
	
	public Game getGame(Long gameKey)
	{
		return SteamCache.instance().getGames().stream()
    			.filter(g -> g.getGameKey() == gameKey).collect(Collectors.toList()).get(0);
	}
	
	public Set<Game> manageGame(Game game) throws Exception
	{
		GameDatabase db = (GameDatabase)DatabaseFactory.getInstance().getDatabase("GAME");
		
		String regex = "[\\p{P}\\p{S}]";
		
		Set<String> filteredGameNames = SteamCache.instance().getGames().stream()
				.map(g -> g.getGameName().toLowerCase().replaceAll(regex, "").replaceAll(" ", "")).collect(Collectors.toSet());
		
		String filteredGameName = game.getGameName().toLowerCase().replaceAll(regex, "").replaceAll(" ", "");
		if(filteredGameNames.contains(filteredGameName))
		{
			Game cachedGame = SteamCache.instance().getGames().stream()
			.filter(g -> g.getGameName().toLowerCase().replaceAll(regex, "").replaceAll(" ", "").equals(filteredGameName))
			.collect(Collectors.toList()).get(0);
			game.setGameKey(cachedGame.getGameKey());
		}
    	
    	if(game.getGameKey() == null)
    	{
    		db.insertGame(game);
    	} else
    	{
    		db.updateGame(game);
    	}
    	return SteamCache.instance().getGames();
	}
	
	public Person getPerson(Long personKey)
	{
		return SteamCache.instance().getPersonList()
    			.stream().filter(p -> p.getPersonKey() == personKey).collect(Collectors.toList()).get(0);
	}
	
	public Set<Person> getPersons()
	{
		return SteamCache.instance().getPersonList();
	}
	
	public Person getAccount() throws Exception
	{
		if(_user == null)
		{
			throw new InvalidSessionException("User not logged in!", _user.getUser().getSessionId());
		}
		
		String userName = _user.getUserName();
		return SteamCache.instance().getPersonList()
				.stream().filter(p -> p.getUserName() == userName).collect(Collectors.toList()).get(0);
	}
	
	public List<Game> getTopFiveGames() throws Exception
	{
		GameDatabase db = (GameDatabase)DatabaseFactory.getInstance().getDatabase("GAME");
    	return db.getTopFiveGames();
	}
	
	public GameOwnership getGameOwnership(Long gameName)
	{
		return SteamCache.instance().getGameOwnership(gameName);
	}
	
	public Set<SteamGame> getFullSteamGameList()
	{
		return SteamCache.instance().getFullSteamGameList();
	}
}

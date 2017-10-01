package computer.lanoel.platform;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import computer.lanoel.communication.LANoelAuth;
import computer.lanoel.communication.User;
import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Suggestion;
import computer.lanoel.contracts.Vote;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.database.GameDatabase;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.platform.database.VoteDatabase;
import computer.lanoel.steam.SteamCache;
import computer.lanoel.steam.contracts.GameOwnership;
import computer.lanoel.steam.contracts.SteamGame;

public class PreEventManager {

	private UserAccount _user;
	private Calendar _voteCutoffTime;
	private VoteDatabase _voteDb = new VoteDatabase();
	private GameDatabase _gameDb = new GameDatabase();
	private PersonDatabase _personDb = new PersonDatabase();
	
	private static final String VOTE_CUTOFF_TIME = "2017-10-31T05:00"; // yyyy-MM-dd'T'HH:mm
	
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



    	if(_gameDb.getGame(vote.getGameKey()) == null)
    	{
    		throw new BadRequestException("Game does not exist");
    	}
    	
    	if(_personDb.getPerson(vote.getPersonKey()) == null)
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
    	
    	List<Vote> votesForPerson = _voteDb.getVotesForPerson(vote.getPersonKey());
    	if(votesForPerson == null || votesForPerson.isEmpty())
    	{
    		_voteDb.insertVote(vote);
    		SteamCache.instance().refreshVotesCache();
    		return;
    	}
    	else
    	{
    		for(Vote recordedVote : votesForPerson)
    		{
    			if(vote.getVoteNumber() == recordedVote.getVoteNumber())
    			{
    				vote.setVoteKey(recordedVote.getVoteKey());
    				_voteDb.updateVote(vote);
    				SteamCache.instance().refreshVotesCache();
    				return;
    			}
    		}    		
    		_voteDb.insertVote(vote);
    	}
    	SteamCache.instance().refreshVotesCache();
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
	
	private String gameNameFilter(String gameName)
	{
		String regex = "[\\p{P}\\p{S}]";
		return gameName.toLowerCase()
				.replaceAll(regex, "").replaceAll(" ", "").replaceAll("'", "")
				.replaceAll("-", "").replaceAll("_", "").replaceAll("!", "")
				.replaceAll("\\.", "");
	}
	
	public Set<Game> manageGame(Game game) throws Exception
	{
		
		Set<String> filteredGameNames = SteamCache.instance().getGames().stream()
				.map(g -> gameNameFilter(g.getGameName())).collect(Collectors.toSet());
		
		String filteredGameName = gameNameFilter(game.getGameName());
		if(filteredGameNames.contains(filteredGameName))
		{
			Game cachedGame = SteamCache.instance().getGames().stream()
			.filter(g -> gameNameFilter(g.getGameName()).equals(filteredGameName))
			.collect(Collectors.toList()).get(0);
			
			String steamGameName = SteamCache.instance().getFullSteamGameList().stream()
					.filter(g -> gameNameFilter(g.getName()).equals(filteredGameName))
					.map(g -> g.getName())
					.collect(Collectors.toList()).get(0);
			game.setGameKey(cachedGame.getGameKey());
			game.setGameName((steamGameName == null || steamGameName == "") ? cachedGame.getGameName() : steamGameName);
		}
    	
    	if(game.getGameKey() == null)
    	{
    		_gameDb.insertGame(game);
    	} else
    	{
    		_gameDb.updateGame(game);
    	}
    	SteamCache.instance().refresh();
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
		Set<Long> gameKeys = _gameDb.getTopFiveGames().stream().map(g -> g.getGameKey()).collect(Collectors.toSet());
		List<Game> gameList = SteamCache.instance().getGames().stream()
			.filter(g -> gameKeys.contains(g.getGameKey())).collect(Collectors.toList());
		Collections.sort(gameList);
		return gameList;
	}
	
	public GameOwnership getGameOwnership(Long gameKey)
	{
        Game game = SteamCache.instance().getGames().stream().filter(g -> g.getGameKey().equals(gameKey)).findFirst().get();
		return SteamCache.instance().getGameOwnership(game);
	}
	
	public List<GameOwnership> getFullGameOwnership()
	{
		return SteamCache.instance().getGameOwnership();
	}
	
	public Set<SteamGame> getFullSteamGameList()
	{
		return SteamCache.instance().getFullSteamGameList();
	}
	
	public List<Suggestion> getSuggestions() throws Exception
	{
		return _personDb.getSuggestions();
	}
	
	public Suggestion manageSuggestion(Suggestion sug) throws Exception
	{
		
		if(sug.Key == null || sug.Key == "")
		{
			_personDb.insertSuggestion(sug);
			return sug;
		}
		
		return _personDb.updateSuggestion(sug);
	}
}

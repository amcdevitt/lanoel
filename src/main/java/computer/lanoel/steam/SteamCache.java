package computer.lanoel.steam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.database.GameDatabase;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.platform.database.VoteDatabase;
import computer.lanoel.steam.contracts.GameOwnership;
import computer.lanoel.steam.contracts.PlayerSteamGame;
import computer.lanoel.steam.contracts.PlayerSteamInformation;
import computer.lanoel.steam.contracts.SteamGame;
import computer.lanoel.steam.models.PlayerGameListResponse;
import computer.lanoel.steam.models.SteamFullListResponse;
import computer.lanoel.steam.models.SteamPlayerSummaryResponse;

public class SteamCache {

	private static SteamCache _cache;
	private Set<Person> _personCache;
	private Set<Game> _lanoelGameCache;
	private Map<String, SteamGame> _steamGameCache;
	private Set<SteamGame> _fullSteamGameSet;
	private Set<Vote> _votesCache;
	private List<GameOwnership> _ownershipCache;
	private VoteDatabase _voteDb = new VoteDatabase();
	private PersonDatabase _personDb = new PersonDatabase();
	private GameDatabase _gameDb = new GameDatabase();
	
	private SteamCache()
	{
		_personCache = InitialPersonInfo.personSet();
		_lanoelGameCache = new HashSet<Game>();
		_steamGameCache = new HashMap<String, SteamGame>();
		_fullSteamGameSet = new HashSet<SteamGame>();
		_votesCache = new HashSet<Vote>();
		_ownershipCache = new ArrayList<>();
	}
	
	public static SteamCache instance()
	{
		if(_cache == null)
		{
			_cache = new SteamCache();
		}
		
		return _cache;
	}
	
	public void refresh() throws Exception
	{
		refreshLanoelGameCache();
		refreshPlayerCache();
		refreshVotesCache();
		refreshGameOwnershipCache();
	}
	
	public void refreshVotesCache() throws Exception
	{
		_votesCache = new HashSet<>(_voteDb.getVotes());
		populateVotes(_votesCache);
	}
	
	private void populateVotes(Set<Vote> votes)
	{
		for(Person person : _personCache)
		{
			Optional<Vote> vote1 = votes.stream()
					.filter(v -> v.getPersonKey() == person.getPersonKey())
					.filter(v -> v.getVoteNumber() == 1).findFirst();
			Optional<Vote> vote2 = votes.stream()
					.filter(v -> v.getPersonKey() == person.getPersonKey())
					.filter(v -> v.getVoteNumber() == 2).findFirst();
			Optional<Vote> vote3 = votes.stream()
					.filter(v -> v.getPersonKey() == person.getPersonKey())
					.filter(v -> v.getVoteNumber() == 3).findFirst();
			
			// Update votes on each person
			vote1.ifPresent(v -> person.setGameVote1(_lanoelGameCache.stream()
					.filter(g -> g.getGameKey() == v.getGameKey()).findFirst().get().getGameName()));
			
			vote2.ifPresent(v -> person.setGameVote2(_lanoelGameCache.stream()
					.filter(g -> g.getGameKey() == v.getGameKey()).findFirst().get().getGameName()));
			
			vote3.ifPresent(v -> person.setGameVote3(_lanoelGameCache.stream()
					.filter(g -> g.getGameKey() == v.getGameKey()).findFirst().get().getGameName()));
			
			Set<Game> uniqueGamesVotedOn = new HashSet<Game>(); 
			
			// Update uniqueVotePersonVotes on Game
			if(vote1.isPresent())
			{
				Game game = _lanoelGameCache.stream().filter(g -> g.getGameKey() == vote1.get().getGameKey()).findFirst()
					.get();
				uniqueGamesVotedOn.add(game);
				game.setNumUniquePersonVotes(game.getNumUniquePersonVotes() + 1);
			}
			
			if(vote2.isPresent())
			{
				Game game = _lanoelGameCache.stream().filter(g -> g.getGameKey() == vote2.get().getGameKey()).findFirst()
					.get();
				if(!uniqueGamesVotedOn.contains(game))
				{
					uniqueGamesVotedOn.add(game);
					game.setNumUniquePersonVotes(game.getNumUniquePersonVotes() + 1);
				}
			}
			
			if(vote3.isPresent())
			{
				Game game = _lanoelGameCache.stream().filter(g -> g.getGameKey() == vote3.get().getGameKey()).findFirst()
					.get();
				if(!uniqueGamesVotedOn.contains(game))
				{
					game.setNumUniquePersonVotes(game.getNumUniquePersonVotes() + 1);
				}
			}
		}
		
		for(Game game : _lanoelGameCache)
		{
			game.setVoteTotal(0);
			Stream<Vote> votesForGame = votes.stream().filter(v -> v.getGameKey() == game.getGameKey());
			votesForGame.forEach(v -> game.setVoteTotal(game.getVoteTotal() + v.getVoteNumber()));
			
			
		}
	}
	
	public void refreshFullSteamGameCache()
	{
		SteamFullListResponse response = SteamService.getFullGameList();
		_fullSteamGameSet = response.applist.apps.app;
		_steamGameCache.clear();
		_steamGameCache.putAll(_fullSteamGameSet.stream().collect(Collectors.toMap(SteamGame::getName, p -> p,(game1, game2) -> {return game1;})));
	}
	
	private void refreshPlayerCache() throws Exception
	{
		_personCache.clear();
		_personCache = InitialPersonInfo.personSet();
		List<Person> personsFromDb = _personDb.getPersonList();
		SteamPlayerSummaryResponse response = 
				SteamService.getPlayerInformationList(
						_personCache.stream().map(p -> p.getSteamInfo().getSteamid())
						.collect(Collectors.toList()));
		
		for(Person person : _personCache)
		{
			for(PlayerSteamInformation info : response.response.players)
			{
				if(person.getSteamInfo().getSteamid().equals(info.getSteamid()))
				{
					person.setSteamInfo(info);
					PlayerGameListResponse playerGameResponse = 
							SteamService.getGamesForPlayer(person.getSteamInfo().getSteamid());
					person.getSteamInfo().setSteamGameList(playerGameResponse.response.games);
					break;
				}
			}
			
			Person personFromDb = personsFromDb.stream()
					.filter(p -> p.getUserName().equals(person.getUserName())).collect(Collectors.toList()).get(0);
			
			person.setInformation(personFromDb.getInformation());
			person.setPersonKey(personFromDb.getPersonKey());
			person.setTitle(personFromDb.getTitle());
		}
	}
	
	private void refreshLanoelGameCache() throws Exception
	{
		List<Game> gameList = _gameDb.getGameList();
		for(Game game : gameList)
		{
			game.setSteamGame(_steamGameCache.get(game.getGameName()));
			if(game.getSteamGame() == null) continue;
			
			game.setSteamInfo(SteamService.getFullGameInformation(game.getSteamGame().getAppid()).data);
		}
		
		_lanoelGameCache.clear();
		_lanoelGameCache.addAll(gameList);
	}
	
	public Set<Game> getGames()
	{
		return _lanoelGameCache;
	}
	
	public GameOwnership getGameOwnership(String gameName)
	{
		List<Game> gameList = _lanoelGameCache.stream()
				.filter(g -> g.getSteamGame().getName().equals(gameName)).collect(Collectors.toList());
		if(gameList.size() == 0)
		{
			return new GameOwnership();
		}
		return getGameOwnership(gameList.get(0));
	}

	private void refreshGameOwnershipCache()
	{
		List<GameOwnership> tempCache = _lanoelGameCache.stream().map(this::getGameOwnership).collect(Collectors.toList());
		_ownershipCache = tempCache;
	}
	
	public List<GameOwnership> getGameOwnership()
	{
		return _ownershipCache;
	}
	
	public GameOwnership getGameOwnership(Game game)
	{
		GameOwnership ownership = new GameOwnership();
		ownership.game = game;
		
		if(game.getSteamGame() == null)
		{
			return ownership;
		}
		
		for(Person person : _personCache)
		{
			boolean hasGame = false;
			for(PlayerSteamGame personGame : person.getSteamInfo().getSteamGameList())
			{
				if(personGame.getAppid().equals(game.getSteamGame().getAppid()))
				{
					ownership.owners.add(person);
					hasGame = true;
					break;
				}
			}
			if(!hasGame)
			{
				ownership.nonOwners.add(person);
			}
		}
		
		return ownership;
	}
	
	public Person getUserFromUserAccount(UserAccount uAcct)
	{
		 return _personCache.stream().filter(p -> p.getUserName().equals(uAcct.getUserName()))
				 .collect(Collectors.toList()).get(0);
	}
	
	public Set<Person> getPersonList()
	{
		return _personCache;
	}
	
	public Set<SteamGame> getFullSteamGameList()
	{
		return _fullSteamGameSet;
	}
}

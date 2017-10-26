package computer.lanoel.steam;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Vote;
import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.PreEventManager;
import computer.lanoel.platform.database.GameDatabase;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.platform.database.VoteDatabase;
import computer.lanoel.steam.contracts.*;
import computer.lanoel.steam.models.PlayerGameListResponse;
import computer.lanoel.steam.models.SteamFullListResponse;
import computer.lanoel.steam.models.SteamGameInformationResponse;
import computer.lanoel.steam.models.SteamPlayerSummaryResponse;

public class SteamCache {

	private static SteamCache _cache;
	private Set<Person> _personCache;
	private Set<Game> _lanoelGameCache;
	private Map<String, List<Long>> _steamGameCache;
	private Set<SteamGame> _fullSteamGameSet;
	private Set<Vote> _votesCache;
	private List<GameOwnership> _ownershipCache;
	private VoteDatabase _voteDb = new VoteDatabase();
	private PersonDatabase _personDb = new PersonDatabase();
	private GameDatabase _gameDb = new GameDatabase();
	
	private SteamCache()
	{
		_personCache = InitialPersonInfo.personSet();
		_lanoelGameCache = new HashSet<>();
		_steamGameCache = new HashMap<>();
		_fullSteamGameSet = new HashSet<>();
		_votesCache = new HashSet<>();
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
		setCostPerPersonForTopFiveGames();
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
		for(SteamGame game : _fullSteamGameSet)
		{
			String filteredName = PreEventManager.gameNameFilter(game.getName());
			if(_steamGameCache.containsKey(filteredName))
			{
				List<Long> appIdList = _steamGameCache.get(filteredName);
				appIdList.add(game.getAppid());
				_steamGameCache.put(filteredName, appIdList);
				continue;
			}
			List<Long> idList = new ArrayList<>();
			idList.add(game.getAppid());
			_steamGameCache.put(filteredName, idList);
		}
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
			List<Long> steamGameAppKeys = _steamGameCache.get(PreEventManager.gameNameFilter(game.getGameName()));
			SteamGameInformation gameInfo = null;
			if(steamGameAppKeys == null)
			{
				continue;
			}
			for(Long appKey : steamGameAppKeys)
			{
				SteamGameInformationResponse info = SteamService.getFullGameInformation(appKey);
				if(info.data == null)
				{
					continue;
				}
				if(gameInfo == null)
				{
					gameInfo = info.data;
				}
				else
				{
					if(info.data.type.equalsIgnoreCase("game"))
					{
						gameInfo = info.data;
					}
				}
			}
			if(gameInfo != null)
			{
				SteamGame sg = new SteamGame();
				sg.setName(game.getGameName());
				sg.setAppid(gameInfo.steam_appid);
				game.setSteamGame(sg);
				game.setSteamInfo(gameInfo);
			}
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
			if(person.getSteamInfo() == null || person.getSteamInfo().getSteamGameList() == null)
			{
				continue;
			}
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

	public void setCostPerPersonForTopFiveGames() throws Exception
	{
		List<Long> topFiveGameKeys = _gameDb.getTopFiveGames().stream().filter(g -> !g.isFree()).map(f -> f.getGameKey()).collect(Collectors.toList());
		for(Person person : _personCache)
		{
			BigDecimal total = new BigDecimal(0);
			for(GameOwnership owner : _ownershipCache.stream().filter(o -> topFiveGameKeys.contains(o.game.getGameKey())).collect(Collectors.toList()))
			{
				if(owner.nonOwners.stream().filter(n -> n.getPersonKey().equals(person.getPersonKey())).findAny().isPresent())
				{
					total = total.add(new BigDecimal(owner.game.getSteamInfo().price_overview.finalPrice));
				}
			}
			person.setPriceToBuyTopFive(total);
		}
	}
}

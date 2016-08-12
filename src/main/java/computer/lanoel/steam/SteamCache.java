package computer.lanoel.steam;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.platform.InitialPersonInfo;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.GameDatabase;
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
	
	private SteamCache()
	{
		_personCache = InitialPersonInfo.personSet();
		_lanoelGameCache = new HashSet<Game>();
		_steamGameCache = new HashMap<String, SteamGame>();
	}
	
	public static SteamCache instance()
	{
		if(_cache == null)
		{
			return new SteamCache();
		}
		
		return _cache;
	}
	
	public void refresh() throws Exception
	{
		refreshFullSteamGameCache();
		refreshLanoelGameCache();
		refreshPlayerCache();
	}
	
	public void refreshFullSteamGameCache()
	{
		SteamFullListResponse response = SteamService.getFullGameList();
		Set<SteamGame> gameSet = response.applist.app;
		_steamGameCache.clear();
		_steamGameCache.putAll(gameSet.stream().collect(Collectors.toMap(SteamGame::getName, p -> p)));
	}
	
	public void refreshPlayerCache()
	{
		SteamPlayerSummaryResponse response = 
				SteamService.getPlayerInformationList(
						_personCache.stream().map(p -> p.getSteamInfo().getSteamid())
						.collect(Collectors.toList()));
		
		for(Person person : _personCache)
		{
			for(PlayerSteamInformation info : response.response.players)
			{
				if(person.getSteamInfo().getSteamid() == info.getSteamid())
				{
					person.setSteamInfo(info);
					PlayerGameListResponse playerGameResponse = 
							SteamService.getGamesForPlayer(person.getSteamInfo().getSteamid());
					person.getSteamInfo().setSteamGameList(playerGameResponse.response.games);
					break;
				}
			}
		}
	}
	
	public void refreshLanoelGameCache() throws Exception
	{
		GameDatabase db = (GameDatabase)DatabaseFactory.getInstance().getDatabase("GAME");
		List<Game> gameList = db.getGameList();
		for(Game game : gameList)
		{
			game.setSteamGame(_steamGameCache.get(game.getGameName()));
			if(game.getSteamGame() == null) continue;
			
			game.setSteamInfo(SteamService.getFullGameInformation(game.getSteamGame().getAppid()));
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
		return getGameOwnership(_lanoelGameCache.stream()
				.filter(g -> g.getSteamGame().getName() == gameName).collect(Collectors.toList()).get(0));
	}
	
	public GameOwnership getGameOwnership(Long gameId)
	{
		return getGameOwnership(_lanoelGameCache.stream()
				.filter(g -> g.getSteamGame().getAppid() == gameId).collect(Collectors.toList()).get(0));
	}
	
	public GameOwnership getGameOwnership(Game game)
	{
		GameOwnership ownership = new GameOwnership();
		ownership.game = game;
		
		for(Person person : _personCache)
		{
			boolean hasGame = false;
			for(PlayerSteamGame personGame : person.getSteamInfo().getSteamGameList())
			{
				if(personGame.getAppid() == game.getSteamGame().getAppid())
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
}

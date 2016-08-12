package computer.lanoel.steam;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.steam.models.PlayerGameListResponse;
import computer.lanoel.steam.models.SteamFullListResponse;
import computer.lanoel.steam.models.SteamPlayerSummaryResponse;

public class SteamService {

	private static final String fullGameListUrl = "http://api.steampowered.com/ISteamApps/GetAppList/v0001/";
	private static final String playerGameListUrl = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
	private static final String playerSummaryUrl = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/";
	private static final String fullGameInformationUrl = "http://store.steampowered.com/api/appdetails";
	
	private static <T> T makeRequest(Class<T> classType, String url, HttpMethod requestType)
	{
		RestTemplate restTemplate = new RestTemplate();
		HttpEntity<String> entity = new HttpEntity<String>(null, null);
		
		ResponseEntity<T> response = restTemplate.exchange(url, requestType, entity, classType);
		return response.getBody();
	}
	
	public static SteamFullListResponse getFullGameList()
	{
		return makeRequest(SteamFullListResponse.class, fullGameListUrl, HttpMethod.GET);
	}
	
	public static SteamPlayerSummaryResponse getPlayerInformationList(List<Long> steamIds)
	{
		String url = playerSummaryUrl + "?key=" + ServiceConstants.steamAPIkey;
		url += "&steamids=";
		url += org.parboiled.common.StringUtils.join(steamIds, ",");

		return makeRequest(SteamPlayerSummaryResponse.class, url, HttpMethod.GET);
	}
	
	public static PlayerGameListResponse getGamesForPlayer(Long steamId)
	{
		String url = playerGameListUrl + "?key=" + ServiceConstants.steamAPIkey;
		url += "&format=json";
		url += "&steamid=";
		url += steamId;

		return makeRequest(PlayerGameListResponse.class, url, HttpMethod.GET);
	}
	
	public static String getFullGameInformation(Long appId)
	{
		String url = fullGameInformationUrl + "?appids=" + appId;
		return makeRequest(String.class, url, HttpMethod.GET);
	}
}

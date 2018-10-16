package computer.lanoel.steam;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import computer.lanoel.platform.ServiceConstants;
import computer.lanoel.steam.models.PlayerGameListResponse;
import computer.lanoel.steam.models.SteamFullListResponse;
import computer.lanoel.steam.models.SteamGameInformationResponse;
import computer.lanoel.steam.models.SteamPlayerSummaryResponse;
import org.apache.commons.lang3.StringUtils;

public class SteamService {

	private static final String fullGameListUrl = "http://api.steampowered.com/ISteamApps/GetAppList/v0001/";
	private static final String playerGameListUrl = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
	private static final String playerSummaryUrl = "http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/";
	private static final String fullGameInformationUrl = "http://store.steampowered.com/api/appdetails";

	private static Gson _gson = new GsonBuilder().create();

	public static SteamFullListResponse getFullGameList() throws Exception
	{
		HttpResponse<String> res = Unirest.get(fullGameListUrl).asString();
		return _gson.fromJson(res.getBody(), SteamFullListResponse.class);
	}
	
	public static SteamPlayerSummaryResponse getPlayerInformationList(List<Long> steamIds) throws Exception
	{
		String url = playerSummaryUrl + "?key=" + ServiceConstants.steamAPIkey;
		url += "&steamids=";
		url += StringUtils.join(steamIds, ",");

		HttpResponse<String> res = Unirest.get(url).asString();
		return _gson.fromJson(res.getBody(), SteamPlayerSummaryResponse.class);
	}
	
	public static PlayerGameListResponse getGamesForPlayer(Long steamId) throws Exception
	{
		String url = playerGameListUrl + "?key=" + ServiceConstants.steamAPIkey;
		url += "&format=json";
		url += "&steamid=";
		url += steamId;

		HttpResponse<String> res = Unirest.get(url).asString();
		return _gson.fromJson(res.getBody(), PlayerGameListResponse.class);
	}
	
	public static SteamGameInformationResponse getFullGameInformation(Long appId) throws Exception
	{
		String url = fullGameInformationUrl + "?appids=" + appId;
		HttpResponse<String> res = Unirest.get(url).asString();

        Type mapType = new TypeToken<Map<String,SteamGameInformationResponse>>(){}.getType();
        Map<String,SteamGameInformationResponse> resObj = _gson.fromJson(res.getBody(), mapType);
		return resObj.get(appId.toString());
	}
}

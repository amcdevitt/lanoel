package computer.lanoel.steam.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import computer.lanoel.steam.models.SteamGamePriceOverview;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SteamGameInformation {

	public String background;
	public SteamGamePriceOverview price_overview;
	public String header_image;
	public String about_the_game;
	public String detailed_description;
	public int steam_appid;
	public String name;
	
}

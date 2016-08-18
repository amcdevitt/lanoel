package computer.lanoel.steam.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SteamGamePriceOverview {

	public int initial;
	
	@JsonProperty("final")
	public int finalPrice;
}

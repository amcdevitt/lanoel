package computer.lanoel.steam.contracts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PlayerGameList {

	public int game_count;
	public List<PlayerSteamGame> games;
}

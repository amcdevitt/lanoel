package computer.lanoel.steam.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PlayerSteamGame {

	private Long appid;
	private Long playtime_forever;
	
	public Long getAppid() {
		return appid;
	}
	
	public void setAppid(Long appid) {
		this.appid = appid;
	}

	public Long getPlaytime_forever() {
		return playtime_forever;
	}

	public void setPlaytime_forever(Long playtime_forever) {
		this.playtime_forever = playtime_forever;
	}
	
}

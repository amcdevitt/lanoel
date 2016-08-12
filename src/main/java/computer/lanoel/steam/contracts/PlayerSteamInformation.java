package computer.lanoel.steam.contracts;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerSteamInformation {

	private Long steamid;
	private String avatar;
	private String avatarmedium;
	private String avatarfull;
	private String personaname;
	private List<PlayerSteamGame> steamGameList;
	
	public PlayerSteamInformation(){}
	public PlayerSteamInformation(Long steamId)
	{
		steamid = steamId;
	}
	
	public Long getSteamid() {
		return steamid;
	}
	
	public void setSteamid(Long steamid) {
		this.steamid = steamid;
	}
	
	public String getAvatar() {
		return avatar;
	}
	
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAvatarmedium() {
		return avatarmedium;
	}

	public void setAvatarmedium(String avatarmedium) {
		this.avatarmedium = avatarmedium;
	}

	public String getAvatarfull() {
		return avatarfull;
	}

	public void setAvatarfull(String avatarfull) {
		this.avatarfull = avatarfull;
	}

	public String getPersonaname() {
		return personaname;
	}

	public void setPersonaname(String personaname) {
		this.personaname = personaname;
	}

	public List<PlayerSteamGame> getSteamGameList() {
		return steamGameList;
	}

	public void setSteamGameList(List<PlayerSteamGame> steamGameList) {
		this.steamGameList = steamGameList;
	}
}

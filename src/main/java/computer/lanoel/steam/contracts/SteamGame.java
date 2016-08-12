package computer.lanoel.steam.contracts;

public class SteamGame {

	private Long appid;
	private String name;
	
	public Long getAppid() {
		return appid;
	}
	
	public void setAppid(Long appid) {
		this.appid = appid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
}

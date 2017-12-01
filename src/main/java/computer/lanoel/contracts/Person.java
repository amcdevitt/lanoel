package computer.lanoel.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import computer.lanoel.steam.contracts.PlayerSteamInformation;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Person
{
	private Long PersonKey;
	private String PersonName;
	private String Title;
	private String Information;
	private String GameVote1;
	private String GameVote2;
	private String GameVote3;
	
	private String userName;
	private PlayerSteamInformation steamInfo;

	private BigDecimal priceToBuyTopFive;
	
	public Person(){}
	
	public Person(String username, PlayerSteamInformation steaminfo)
	{
		userName = username;
		steamInfo = steaminfo;
	}
	
	public Long getPersonKey()
	{
		return PersonKey;
	}
	public void setPersonKey(Long personKey)
	{
		PersonKey = personKey;
	}
	
	public String getPersonName()
	{
		return PersonName;
	}
	
	public Person setPersonName(String personName)
	{
		PersonName = personName;
		return this;
	}
	
	public String getTitle()
	{
		return Title;
	}
	
	public void setTitle(String title)
	{
		Title = title;
	}
	
	public String getInformation()
	{
		return Information;
	}
	
	public void setInformation(String information)
	{
		Information = information;
	}
	public String getGameVote1()
	{
		return GameVote1;
	}
	public void setGameVote1(String gameVote1)
	{
		GameVote1 = gameVote1;
	}
	public String getGameVote2()
	{
		return GameVote2;
	}
	public void setGameVote2(String gameVote2)
	{
		GameVote2 = gameVote2;
	}
	public String getGameVote3()
	{
		return GameVote3;
	}
	public void setGameVote3(String gameVote3)
	{
		GameVote3 = gameVote3;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + userName.hashCode();
		result = prime * result + PersonName.hashCode();
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		return this.userName == other.userName && this.PersonName == other.PersonName;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public PlayerSteamInformation getSteamInfo() {
		return steamInfo;
	}
	public void setSteamInfo(PlayerSteamInformation steamInfo) {
		this.steamInfo = steamInfo;
	}

	public BigDecimal getPriceToBuyTopFive() {
		return priceToBuyTopFive;
	}

	public void setPriceToBuyTopFive(BigDecimal priceToBuyTopFive) {
		if(priceToBuyTopFive == null || priceToBuyTopFive.doubleValue() == 0)
		{
			this.priceToBuyTopFive = new BigDecimal(0);
		}
		this.priceToBuyTopFive = priceToBuyTopFive;
	}
}

package computer.lanoel.contracts;

public class Person
{
	private Long PersonKey;
	private String PersonName;
	private String Title;
	private String Information;
	private String GameVote1;
	private String GameVote2;
	private String GameVote3;
	
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
	
	public void setPersonName(String personName)
	{
		PersonName = personName;
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
	
}

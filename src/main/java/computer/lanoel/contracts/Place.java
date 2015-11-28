package computer.lanoel.contracts;

import computer.lanoel.platform.ServiceUtils;

public class Place
{
	private int place;
	private String person;
	private int pointValue;
	
	private void populatePointValues() throws Exception
	{
		try
		{
			pointValue = ServiceUtils.storage().getPointValues().get(place);
		} catch (Exception e)
		{
			pointValue = 0;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + place;
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
		Place other = (Place) obj;
		if (place != other.place)
			return false;
		return true;
	}

	public String getPerson()
	{
		return person;
	}

	public void setPerson(String person)
	{
		this.person = person;
	}

	public int getPointValue()
	{
		return pointValue;
	}
	
	public void setPlace(int newPlace) throws Exception
	{
		place = newPlace;
		populatePointValues();
	}
	
	public int getPlace()
	{
		return place;
	}
}

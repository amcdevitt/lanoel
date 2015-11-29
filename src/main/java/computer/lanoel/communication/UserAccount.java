package computer.lanoel.communication;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class UserAccount {

	private PersonInfo BillingAddress;
	private PersonInfo ShippingAddress;
	private String UserName;
	private String Organization;
	private User user;
	
	public UserAccount()
	{
	}
	
	/**
	 * @return the billingAddress
	 */
	public PersonInfo getBillingAddress() {
		return BillingAddress;
	}
	
	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public Long getBillingAddressKey()
	{
		return BillingAddress == null ? null : BillingAddress.getPersonInfoKey();
	}
	
	/**
	 * @param billingAddress the billingAddress to set
	 */
	public void setBillingAddress(PersonInfo billingAddress) {
		BillingAddress = billingAddress;
	}
	/**
	 * @return the mailingAddress
	 */
	public PersonInfo getShippingAddress() {
		return ShippingAddress;
	}
	
	/**
	 * 
	 * @return
	 */
	@JsonIgnore
	public Long getShippingAddressKey()
	{
		return ShippingAddress == null ? null : ShippingAddress.getPersonInfoKey();
	}
	
	/**
	 * @param mailingAddress the mailingAddress to set
	 */
	public void setShippingAddress(PersonInfo mailingAddress) {
		ShippingAddress = mailingAddress;
	}
	
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return UserName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		UserName = userName;
	}
	
	public boolean equals(Object obj)
	{
		UserAccount other;
		try
		{
			other = (UserAccount)obj;
		}
		catch(Exception e)
		{
			return false;
		}
		
		return 	this.BillingAddress.equals(other.BillingAddress) &&
				this.ShippingAddress.equals(other.ShippingAddress) &&
				this.UserName.equals(other.UserName);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserAccount [BillingAddress=" + BillingAddress + ", MailingAddress="
				+ ShippingAddress  + ", UserName=" + UserName + "]";
	}

	public String getOrganization()
	{
		return Organization;
	}

	public void setOrganization(String organization)
	{
		Organization = organization;
	}

	public User getUser()
	{
		return user;
	}

	public void setUser(User user)
	{
		this.user = user;
	}
}

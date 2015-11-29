package computer.lanoel.communication;

import java.util.Calendar;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PersonInfo {

	private Long PersonInfoKey;
	private String FirstName;
	private String LastName;
	private String MiddleName;
	private String AddressLine1;
	private String AddressLine2;
	private String AddressLine3;
	private String AddressLine4;
	private String AddressLine5;
	private String AddressLine6;
	private String City;
	private String State;
	private String PostalCode;
	private String Email;
	private String AlternateEmail;
	private String HomePhone;
	private String WorkPhone;
	private String CellPhone;
	private Calendar LastModified;
	
	/**
	 * @return the personInfoKey
	 */
	public Long getPersonInfoKey() {
		return PersonInfoKey;
	}
	/**
	 * @param personInfoKey the personInfoKey to set
	 */
	public void setPersonInfoKey(Long personInfoKey) {
		PersonInfoKey = personInfoKey;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return FirstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return LastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	/**
	 * @return the middleName
	 */
	public String getMiddleName() {
		return MiddleName;
	}
	/**
	 * @param middleName the middleName to set
	 */
	public void setMiddleName(String middleName) {
		MiddleName = middleName;
	}
	/**
	 * @return the addressLine1
	 */
	public String getAddressLine1() {
		return AddressLine1;
	}
	/**
	 * @param addressLine1 the addressLine1 to set
	 */
	public void setAddressLine1(String addressLine1) {
		AddressLine1 = addressLine1;
	}
	/**
	 * @return the addressLine2
	 */
	public String getAddressLine2() {
		return AddressLine2;
	}
	/**
	 * @param addressLine2 the addressLine2 to set
	 */
	public void setAddressLine2(String addressLine2) {
		AddressLine2 = addressLine2;
	}
	/**
	 * @return the addressLine3
	 */
	public String getAddressLine3() {
		return AddressLine3;
	}
	/**
	 * @param addressLine3 the addressLine3 to set
	 */
	public void setAddressLine3(String addressLine3) {
		AddressLine3 = addressLine3;
	}
	/**
	 * @return the addressLine4
	 */
	public String getAddressLine4() {
		return AddressLine4;
	}
	/**
	 * @param addressLine4 the addressLine4 to set
	 */
	public void setAddressLine4(String addressLine4) {
		AddressLine4 = addressLine4;
	}
	/**
	 * @return the addressLine5
	 */
	public String getAddressLine5() {
		return AddressLine5;
	}
	/**
	 * @param addressLine5 the addressLine5 to set
	 */
	public void setAddressLine5(String addressLine5) {
		AddressLine5 = addressLine5;
	}
	/**
	 * @return the addressLine6
	 */
	public String getAddressLine6() {
		return AddressLine6;
	}
	/**
	 * @param addressLine6 the addressLine6 to set
	 */
	public void setAddressLine6(String addressLine6) {
		AddressLine6 = addressLine6;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return City;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		City = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return State;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		State = state;
	}
	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return PostalCode;
	}
	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(String postalCode) {
		PostalCode = postalCode;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return Email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		Email = email;
	}
	/**
	 * @return the alternateEmail
	 */
	public String getAlternateEmail() {
		return AlternateEmail;
	}
	/**
	 * @param alternateEmail the alternateEmail to set
	 */
	public void setAlternateEmail(String alternateEmail) {
		AlternateEmail = alternateEmail;
	}
	/**
	 * @return the homePhone
	 */
	public String getHomePhone() {
		return HomePhone;
	}
	/**
	 * @param homePhone the homePhone to set
	 */
	public void setHomePhone(String homePhone) {
		HomePhone = homePhone;
	}
	/**
	 * @return the workPhone
	 */
	public String getWorkPhone() {
		return WorkPhone;
	}
	/**
	 * @param workPhone the workPhone to set
	 */
	public void setWorkPhone(String workPhone) {
		WorkPhone = workPhone;
	}
	/**
	 * @return the cellPhone
	 */
	public String getCellPhone() {
		return CellPhone;
	}
	/**
	 * @param cellPhone the cellPhone to set
	 */
	public void setCellPhone(String cellPhone) {
		CellPhone = cellPhone;
	}
	
	/**
	 * @return the lastModified
	 */
	public Calendar getLastModified() {
		return LastModified;
	}

	/**
	 * @param lastModified the lastModified to set
	 */
	public void setLastModified(Calendar lastModified) {
		LastModified = lastModified;
	}
	
	public void setLastModifiedFromSql(java.sql.Timestamp time)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time.getTime());
		LastModified = cal;
	}

	public boolean equals(Object obj)
	{
		PersonInfo other;
		try
		{
			other = (PersonInfo)obj;
		}
		catch(Exception e)
		{
			return false;
		}
		
		return this.PersonInfoKey.equals(other.PersonInfoKey) &&
				this.FirstName == other.FirstName &&
				this.LastName == other.LastName &&
				this.MiddleName == other.MiddleName &&
				this.AddressLine1 == other.AddressLine1 &&
				this.AddressLine2 == other.AddressLine2 &&
				this.AddressLine3 == other.AddressLine3 &&
				this.AddressLine4 == other.AddressLine4 &&
				this.AddressLine5 == other.AddressLine5 &&
				this.AddressLine6 == other.AddressLine6 &&
				this.City == other.City &&
				this.State == other.State &&
				this.PostalCode == other.PostalCode &&
				this.Email == other.Email &&
				this.AlternateEmail == other.AlternateEmail &&
				this.HomePhone == other.HomePhone &&
				this.WorkPhone == other.WorkPhone &&
				this.CellPhone == other.CellPhone;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "PersonInfo [PersonInfoKey=" + PersonInfoKey + ", FirstName="
				+ FirstName + ", LastName=" + LastName + ", MiddleName="
				+ MiddleName + ", AddressLine1=" + AddressLine1
				+ ", AddressLine2=" + AddressLine2 + ", AddressLine3="
				+ AddressLine3 + ", AddressLine4=" + AddressLine4
				+ ", AddressLine5=" + AddressLine5 + ", AddressLine6="
				+ AddressLine6 + ", City=" + City + ", State=" + State
				+ ", PostalCode=" + PostalCode + ", Email=" + Email
				+ ", AlternateEmail=" + AlternateEmail + ", HomePhone="
				+ HomePhone + ", WorkPhone=" + WorkPhone + ", CellPhone="
				+ CellPhone + "]";
	}
}

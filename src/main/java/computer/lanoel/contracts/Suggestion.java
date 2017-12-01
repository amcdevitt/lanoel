package computer.lanoel.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Suggestion {

	public String Key;
	public String Description;
	public String Category;
}

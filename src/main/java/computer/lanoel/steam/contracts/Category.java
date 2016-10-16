package computer.lanoel.steam.contracts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Category {

	public String description;
}

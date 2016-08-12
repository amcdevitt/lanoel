package computer.lanoel.communication;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class UserAccountManagerClient
{
	private String BaseUrl;
	
	public UserAccountManagerClient(String baseUrl)
	{
		BaseUrl = baseUrl;
	}
	
	public UserAccount getUserAccount(String sessionid) throws Exception
	{
		String url = BaseUrl + "/internal/user";
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add("sessionid", sessionid);
		HttpEntity<String> entity = new HttpEntity<String>(null, headers);
		
		ResponseEntity<UserAccount> response = restTemplate.exchange(url, HttpMethod.GET, entity, UserAccount.class);
		UserAccount uAcct = response.getBody();
		uAcct.setUser(new User(null, response.getHeaders().get("sessionid").get(0)));
		return uAcct;
	}
}

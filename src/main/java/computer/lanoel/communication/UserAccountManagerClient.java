package computer.lanoel.communication;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.omegasixcloud.contracts.accounts.UserAccount;

public class UserAccountManagerClient
{
	private String BaseUrl;
	private Gson _gson;
	
	public UserAccountManagerClient(String baseUrl)
	{
		_gson = new GsonBuilder().create();
		BaseUrl = baseUrl;
	}
	
	public UserAccount getUserAccount(String sessionid) throws Exception
	{
		String url = BaseUrl + "/accounts/user";
		HttpResponse<String> res = Unirest.get(url)
				.header("sessionid", sessionid)
				.asString();
		return _gson.fromJson(res.getBody(), UserAccount.class);
	}
}

package computer.lanoel.communication;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

public class ResponseObject {

	public String message;
	@JsonUnwrapped
	public Map<String, String> data;
	
	public ResponseObject()
	{
		data = new HashMap<String, String>();
	}
	
	public void addData(String key, String value)
	{
		data.put(key, value);
	}
	
}

package computer.lanoel.steam;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import computer.lanoel.steam.models.SteamGameInformationResponse;
import computer.lanoel.steam.models.SteamGameInformationResponseHeader;

public class GameInformationDeserializer extends JsonDeserializer<SteamGameInformationResponseHeader>{

	@Override
	public SteamGameInformationResponseHeader deserialize(JsonParser arg0, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
		
		arg0.nextToken();
		arg0.nextToken();
		SteamGameInformationResponse res = arg0.readValueAs(SteamGameInformationResponse.class);
		SteamGameInformationResponseHeader header = new SteamGameInformationResponseHeader();
		header.response = res;
		return header;	
	}

}

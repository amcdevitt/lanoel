package computer.lanoel.steam.models;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import computer.lanoel.steam.GameInformationDeserializer;

@JsonDeserialize(using = GameInformationDeserializer.class)
public class SteamGameInformationResponseHeader {

	public SteamGameInformationResponse response;
}

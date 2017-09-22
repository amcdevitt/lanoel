package computer.lanoel.controllers;

import computer.lanoel.communication.HttpHelper;
import computer.lanoel.contracts.Tournaments.Swiss.TournamentSwiss;
import computer.lanoel.platform.TournamentSwissManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tournament/swiss")
public class TournamentSwissController {

    @RequestMapping(
    		value = "/{tournamentKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TournamentSwiss> getTournament(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey) 
    				throws Exception
    {
		TournamentSwissManager tsm = new TournamentSwissManager();
    	return new ResponseEntity<>(tsm.getTournamentDetails(tournamentKey), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }

	@RequestMapping(
			value = "/create",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TournamentSwiss> createTournament(
			@RequestHeader(required = false) HttpHeaders requestHeaders)
			throws Exception
	{
		TournamentSwissManager tsm = new TournamentSwissManager();
		return new ResponseEntity<>(tsm.createTournament(), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}

	@RequestMapping(
			value = "/{tournamentKey}/player/{playerName}",
			method = RequestMethod.PUT,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TournamentSwiss> addPlayer(
			@RequestHeader(required = false) HttpHeaders requestHeaders,
			@PathVariable Long tournamentKey, @PathVariable String playerName)
			throws Exception
	{
		TournamentSwissManager tsm = new TournamentSwissManager();
		return new ResponseEntity<>(tsm.addPlayer(playerName, tournamentKey), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}

	@RequestMapping(
			value = "/{tournamentKey}/player/{playerKey}",
			method = RequestMethod.DELETE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TournamentSwiss> removePlayer(
			@RequestHeader(required = false) HttpHeaders requestHeaders,
			@PathVariable Long tournamentKey, @PathVariable Long playerKey)
			throws Exception
	{
		TournamentSwissManager tsm = new TournamentSwissManager();
		return new ResponseEntity<>(tsm.removePlayer(playerKey, tournamentKey), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}

    
}

package computer.lanoel.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import computer.lanoel.contracts.Tournaments.Lanoel.TournamentLanoel;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import computer.lanoel.communication.HttpHelper;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Tournaments.Lanoel.Round;
import computer.lanoel.platform.LanoelManager;
import computer.lanoel.platform.database.TournamentLanoelDatabase;

@RestController
@RequestMapping("/tournament")
public class LanoelController {

	public int _requestCount = 0;
	
	public LanoelController()
	{
	}
	/*
	@ExceptionHandler(InvalidSessionException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<Object> HandleSessionError(Exception e)
	{
		ResponseObject ro = new ResponseObject();
		ro.message = e.getMessage();
		return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<Object> HandleBadRequestError(Exception e)
	{
		ResponseObject ro = new ResponseObject();
		ro.message = e.getMessage();
		return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> HandleDefaultError(Exception e)
	{
		ResponseObject ro = new ResponseObject();
		ro.message = e.getMessage();
 		return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}
*/
    @RequestMapping(
    		value = "/{tournamentKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TournamentLanoel> getTournament(
    		@PathVariable Long tournamentKey,
			HttpServletRequest request)	throws Exception
    {
		LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<>(tm.getLanoelTournament(tournamentKey), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentName}",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createTournament(
    		@PathVariable String tournamentName, HttpServletRequest request) 
    				throws Exception
    { 
    	LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	Long tournamentId = tm.createTournament(tournamentName);
    	return new ResponseEntity<Long>(tournamentId, HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
    }

	@RequestMapping(
			value = "/{tournamentKey}/addParticipants",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TournamentLanoel> addParticipants(
			@PathVariable Long tournamentKey,
			@RequestBody List<TournamentParticipant> participantList,
			HttpServletRequest request)
			throws Exception
	{
		LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
		return new ResponseEntity<>(tm.manageParticipants(tournamentKey, participantList), HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
	}

	@RequestMapping(
			value = "/{tournamentKey}/{participantKey}",
			method = RequestMethod.DELETE)
	public ResponseEntity<TournamentLanoel> removeParticipant(
			@PathVariable Long tournamentKey,
			@PathVariable Long participantKey,
			HttpServletRequest request)
			throws Exception
	{
		LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
		return new ResponseEntity<>(tm.removeParticipant(tournamentKey, participantKey),
				HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
	}
    
    @RequestMapping(
    		value = "/{tournamentKey}/round",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createRound(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@RequestBody Round round, HttpServletRequest request) 
    				throws Exception
    {
    	LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	tm.createRound(round, tournamentKey);
    	
    	return new ResponseEntity<Object>(null, HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentKey}/{roundNumber}/{personKey}/{place}",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> recordResult(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@PathVariable int roundNumber, @PathVariable Long personKey, @PathVariable int place, HttpServletRequest request) 
    				throws Exception
    {
    	LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	tm.recordResult(tournamentKey, personKey, roundNumber, place);
    	return new ResponseEntity<Object>(null, HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentKey}/{roundNumber}/updateScores",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> updateScores(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@PathVariable int roundNumber, @RequestBody List<Place> places, HttpServletRequest request) 
    				throws Exception
    {
    	LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	tm.updateScores(tournamentKey, roundNumber, places);
    	return new ResponseEntity<Object>(null, HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentKey}/{roundNumber}/resetRoundScores",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> resetRoundScores(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@PathVariable int roundNumber, HttpServletRequest request) 
    				throws Exception
    { 
    	LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	tm.resetRoundScores(tournamentKey, roundNumber);
    	
    	return new ResponseEntity<Object>(null, HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/pointValues",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setPointValues(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, 
    		@RequestBody Map<Integer, Integer> pointMap, HttpServletRequest request) 
    				throws Exception
    {
    	LanoelManager tm = new LanoelManager(HttpHelper.getUserFromRequest(request));
    	tm.setPointValues(pointMap);
    	return new ResponseEntity<String>("success", HttpHelper.commonHttpHeaders(tm.getSessionIdForUser()), HttpStatus.OK);
    }
    
}

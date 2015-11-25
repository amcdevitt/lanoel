package computer.lanoel.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import computer.lanoel.communication.ResponseObject;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Round;
import computer.lanoel.contracts.Score;
import computer.lanoel.contracts.Tournament;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.HttpHelper;
import computer.lanoel.platform.ServiceUtils;

@RestController
@RequestMapping("/tournament")
public class TournamentController {

	public int _requestCount = 0;
	
	public TournamentController()
	{
	}
	
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

    @RequestMapping(
    		value = "/{tournamentKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Tournament> getTournament(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey) 
    				throws Exception
    { 
    	Tournament tourn = ServiceUtils.storage().getTournament(tournamentKey);
    	return new ResponseEntity<Tournament>(tourn, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentName}",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createTournament(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable String tournamentName) 
    				throws Exception
    { 
    	//TODO: Add security
    	Tournament tourn = new Tournament();
    	tourn.setTournamentName(tournamentName);
    	Long tournamentId = ServiceUtils.storage().insertTournament(tourn);
    	return new ResponseEntity<Long>(tournamentId, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentKey}/round",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createRound(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@RequestBody Round round) 
    				throws Exception
    { 
    	//TODO: Add security
    	if(round.getGame() == null)
    	{
    		throw new BadRequestException("Please provide a game");
    	}
    	
    	if(ServiceUtils.storage().getGame(round.getGame().getGameKey()) == null)
    	{
    		throw new BadRequestException("Game does not exist");
    	}
    	
    	if(round.getRoundNumber() <= 0)
    	{
    		throw new BadRequestException("Please provide a valid round number");
    	}
    	
    	List<Round> existingRounds = ServiceUtils.storage().getRounds(tournamentKey);
    	if(existingRounds.contains(round))
    	{
    		ServiceUtils.storage().updateRound(tournamentKey, round);
    	}
    	else
    	{
    		ServiceUtils.storage().insertRound(tournamentKey, round);
    	}
    	
    	return new ResponseEntity<Long>(null, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentKey}/{roundNumber}/{personKey}/{place}",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> recordResult(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@PathVariable int roundNumber, @PathVariable Long personKey, @PathVariable int place) 
    				throws Exception
    { 
    	//TODO: Add security
    	List<Round> roundList = ServiceUtils.storage().getRounds(tournamentKey);
    	Round tempRound = new Round();
    	tempRound.setRoundNumber(roundNumber);
    	Round round = null;
    	
    	try
    	{
    		round = roundList.get(roundList.indexOf(tempRound));
    	} catch (Exception e)
    	{
    		throw new Exception("Round " + roundNumber + " does not exist");
    	}
    		
    	ServiceUtils.storage().insertRoundStanding(personKey, round.getRoundKey(), place);
    	/*
    	Map<Integer, Person> roundStandings = ServiceUtils.storage().getRoundStandings(round.getRoundKey());
    	if(!roundStandings.containsKey(place))
    	{
    		
    	}
    	
    	if(roundStandings.get(place21312) != ServiceUtils.storage().getPerson(personKey))
		{
			
		}
    	*/
    	
    	
    	return new ResponseEntity<String>("success", HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/pointValues",
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> setPointValues(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @RequestBody Map<Integer, Integer> pointMap) 
    				throws Exception
    { 
    	//TODO: Add security
    	ServiceUtils.storage().updatePointValues(pointMap);
    	return new ResponseEntity<String>("success", HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/{tournamentKey}/standings",
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getStandings(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey) 
    				throws Exception
    {
    	Tournament tourn = ServiceUtils.storage().getTournament(tournamentKey);
    	Score score = new Score();
    	score.setTournamentName(tourn.getTournamentName());
    	score.setRounds(tourn.getRounds());
    	score.populateScore();
    	
    	return new ResponseEntity<Object>(score, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
}
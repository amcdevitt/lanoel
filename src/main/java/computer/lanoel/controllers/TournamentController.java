package computer.lanoel.controllers;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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

import computer.lanoel.communication.Authorization;
import computer.lanoel.communication.HttpHelper;
import computer.lanoel.communication.ResponseObject;
import computer.lanoel.communication.User;
import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Round;
import computer.lanoel.contracts.Tournament;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
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
    	tourn.populateScore();
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
    public ResponseEntity<Object> createRound(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long tournamentKey, 
    		@RequestBody Round round, HttpServletRequest request) 
    				throws Exception
    { 
    	//TODO: Add security
    	User user = HttpHelper.getUserFromRequest(request);
    	UserAccount uAcct = null;
    	try
    	{
	    	uAcct = Authorization.validateUser(user);
	    	
    	} catch (Exception e)
    	{
    		ResponseObject ro = new ResponseObject();
        	ro.message = "User not logged in!";

        	return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.BAD_REQUEST);
    	}
    	
    	if(!Authorization.userHasAccess(user))
		{
    		throw new InvalidSessionException("User " + user.getUserName() + " does not have access to this api.", user.getSessionId());
		}
    	
    	
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
    	
    	return new ResponseEntity<Object>(null, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.OK);
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
    	//TODO: Add security
    	User user = HttpHelper.getUserFromRequest(request);
    	UserAccount uAcct = null;
    	try
    	{
	    	uAcct = Authorization.validateUser(user);
	    	
    	} catch (Exception e)
    	{
    		ResponseObject ro = new ResponseObject();
        	ro.message = "User not logged in!";

        	return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.BAD_REQUEST);
    	}
    	
    	if(!Authorization.userHasAccess(user))
		{
    		throw new InvalidSessionException("User " + user.getUserName() + " does not have access to this api.", user.getSessionId());
		}
    	
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
    	
    	return new ResponseEntity<Object>("success", HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.OK);
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
    	//TODO: Add security
    	User user = HttpHelper.getUserFromRequest(request);
    	UserAccount uAcct = null;
    	try
    	{
	    	uAcct = Authorization.validateUser(user);
	    	
    	} catch (Exception e)
    	{
    		ResponseObject ro = new ResponseObject();
        	ro.message = "User not logged in!";

        	return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.BAD_REQUEST);
    	}
    	
    	if(!Authorization.userHasAccess(user))
		{
    		throw new InvalidSessionException("User " + user.getUserName() + " does not have access to this api.", user.getSessionId());
		}
    	
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
    		
    	try
    	{
    		ServiceUtils.storage().replaceRoundStandings(round.getRoundKey(), places);
    	} catch (Exception e)
    	{
    		ResponseObject ro = new ResponseObject();
        	ro.message = "Failed";
        	ro.data.put(e.getMessage(), e.getStackTrace().toString());
    		return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity<Object>("success", HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.OK);
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
    	//TODO: Add security
    	User user = HttpHelper.getUserFromRequest(request);
    	UserAccount uAcct = null;
    	try
    	{
	    	uAcct = Authorization.validateUser(user);
	    	
    	} catch (Exception e)
    	{
    		ResponseObject ro = new ResponseObject();
        	ro.message = "User not logged in!";

        	return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.BAD_REQUEST);
    	}
    	
    	if(!Authorization.userHasAccess(user))
		{
    		throw new InvalidSessionException("User " + user.getUserName() + " does not have access to this api.", user.getSessionId());
		}
    	
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
    		
    	try
    	{
    		ServiceUtils.storage().resetRoundStandings(round.getRoundKey());
    	} catch (Exception e)
    	{
    		ResponseObject ro = new ResponseObject();
        	ro.message = "Failed";
        	ro.data.put(e.getMessage(), e.getStackTrace().toString());
    		return new ResponseEntity<Object>(ro, HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.BAD_REQUEST);
    	}
    	
    	return new ResponseEntity<Object>("success", HttpHelper.commonHttpHeaders(user.getSessionId()), HttpStatus.OK);
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
    
}

package computer.lanoel.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Vote;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.DatabaseManager;
import computer.lanoel.platform.HttpHelper;
import computer.lanoel.platform.ServiceUtils;

@RestController
@RequestMapping("/lanoel")
public class CommonController {

	public int _requestCount = 0;
	
	public CommonController()
	{
	}
	
	@ExceptionHandler(InvalidSessionException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<String> HandleSessionError(Exception e)
	{
		return new ResponseEntity<String>("Not Authorized", HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<String> HandleBadRequestError(Exception e)
	{
		return new ResponseEntity<String>(e.getMessage(), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<String> HandleDefaultError(Exception e)
	{
 		return new ResponseEntity<String>(e.getMessage(), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	}
	
	/**
	 * Service health check
	 * @return true if the service is able to fulfill requests, false otherwise
	 * @throws Exception 
	 */
    @RequestMapping(value = "/healthcheck", method = RequestMethod.GET)
    public ResponseEntity<String> healthCheck() throws Exception {
    	
    	HttpStatus status = HttpStatus.OK;
    	String responseMessage = "Hello";
    	if(!ServiceUtils.storage().storageAvailable())
    	{
    		status = HttpStatus.SERVICE_UNAVAILABLE;
    		responseMessage = "Unavailable";
    	}
		return new ResponseEntity<String>(responseMessage, HttpHelper.commonHttpHeaders(), status);
    }
    

    @RequestMapping(
    		value = "/gamelist", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Game>> getGameList(
    		@RequestHeader(required = false) HttpHeaders requestHeaders) throws NumberFormatException, Exception
    { 
    	List<Game> gameList = ServiceUtils.storage().getGameList();
    	return new ResponseEntity<List<Game>>(gameList, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/game/{gameKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> getGame(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long gameKey) 
    				throws NumberFormatException, Exception
    { 
    	Game game = ServiceUtils.storage().getGame(gameKey);
    	return new ResponseEntity<Game>(game, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/game", 
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> manageGame(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @RequestBody Game game) 
    				throws NumberFormatException, Exception
    { 
    	List<Game> currentGames = ServiceUtils.storage().getGameList();
    	if(currentGames != null && !currentGames.isEmpty())
    	{
    		for(Game currentGame : currentGames)
	    	{
	    		if(game.getGameName().equals(currentGame.getGameName()))
	    		{
	    			game.setGameKey(currentGame.getGameKey());
	    			ServiceUtils.storage().updateGame(game);
	    			return new ResponseEntity<Game>(game, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	    		}
	    	}
    	}
       	ServiceUtils.storage().insertGame(game);
    	return new ResponseEntity<Game>(game, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/person", 
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> managePerson(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @RequestBody Person person) 
    				throws NumberFormatException, Exception
    { 
    	List<Person> currentPersons = ServiceUtils.storage().getPersonList();
    	if(currentPersons != null && !currentPersons.isEmpty())
    	{
	    	for(Person currentPerson : currentPersons)
	    	{
	    		if(person.getPersonName().equals(currentPerson.getPersonName()))
	    		{
	    			person.setPersonKey(currentPerson.getPersonKey());
	    			ServiceUtils.storage().updatePerson(person);
	    			return new ResponseEntity<Long>(person.getPersonKey(), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
	    		}
	    	}
    	}
    	ServiceUtils.storage().insertPerson(person);
    	return new ResponseEntity<Long>(person.getPersonKey(), HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/person/{personKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getPerson(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long personKey) 
    				throws NumberFormatException, Exception
    { 
    	Person person = ServiceUtils.storage().getPerson(personKey);
    	return new ResponseEntity<Person>(person, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/personlist", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Person>> getPersonList(
    		@RequestHeader(required = false) HttpHeaders requestHeaders) throws NumberFormatException, Exception
    { 
    	List<Person> personList = ServiceUtils.storage().getPersonList();
    	return new ResponseEntity<List<Person>>(personList, HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/person/{personKey}/vote", 
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> vote(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long personKey, @RequestBody Vote vote) 
    				throws NumberFormatException, Exception
    { 
    	if(vote.getGameKey() == null) throw new BadRequestException("no game provided");
    	if(vote.getPersonKey() == null) throw new BadRequestException("no person provided");
    	
    	if(ServiceUtils.storage().getGame(vote.getGameKey()) == null)
    	{
    		throw new BadRequestException("Game does not exist");
    	}
    	
    	if(ServiceUtils.storage().getPerson(vote.getPersonKey()) == null)
    	{
    		throw new BadRequestException("Person does not exist");
    	}
    	
    	if(vote.getVoteNumber() < 1 || vote.getVoteNumber() > 3)
    	{
    		throw new BadRequestException("Votes must be 1, 2, or 3");
    	}
    	
    	List<Vote> votesForPerson = ServiceUtils.storage().getVotesForPerson(vote.getPersonKey());
    	if(votesForPerson.isEmpty())
    	{
    		ServiceUtils.storage().insertVote(vote);
    	}
    	else
    	{
    		for(Vote recordedVote : votesForPerson)
    		{
    			if(vote.getVoteNumber() == recordedVote.getVoteNumber())
    			{
    				vote.setVoteKey(recordedVote.getVoteKey());
    				ServiceUtils.storage().updateVote(vote);
    				return new ResponseEntity<String>("Vote counted", HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    			}
    		}
    		
    		ServiceUtils.storage().insertVote(vote);
    		return new ResponseEntity<String>("Vote counted", HttpHelper.commonHttpHeaders(), HttpStatus.OK);
    	}
    	return new ResponseEntity<String>("Error voting!", HttpHelper.commonHttpHeaders(), HttpStatus.BAD_REQUEST);
    }
}

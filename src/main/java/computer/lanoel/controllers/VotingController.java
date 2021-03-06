package computer.lanoel.controllers;

import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import computer.lanoel.communication.HttpHelper;
import computer.lanoel.communication.ResponseObject;
import computer.lanoel.contracts.Game;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Suggestion;
import computer.lanoel.contracts.Vote;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.PreEventManager;
import computer.lanoel.steam.SteamCache;
import computer.lanoel.steam.contracts.GameOwnership;
import computer.lanoel.steam.contracts.SteamGame;

@RestController
@RequestMapping("/lanoel")
public class VotingController {

	public int _requestCount = 0;
	
	public VotingController()
	{
	}
	
	@ExceptionHandler(InvalidSessionException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	public ResponseEntity<ResponseObject> HandleSessionError(Exception e)
	{
		ResponseObject ro = new ResponseObject();
		ro.message = e.getMessage();
		return new ResponseEntity<ResponseObject>(ro, HttpHelper.commonHttpHeaders(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ResponseObject> HandleBadRequestError(Exception e)
	{
		ResponseObject ro = new ResponseObject();
		ro.message = e.getMessage();
		return new ResponseEntity<ResponseObject>(ro, HttpHelper.commonHttpHeaders(), HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<ResponseObject> HandleDefaultError(Exception e)
	{
		ResponseObject ro = new ResponseObject();
		ro.message = e.getMessage();
 		return new ResponseEntity<ResponseObject>(ro, HttpHelper.commonHttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
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
		return new ResponseEntity<>(responseMessage, HttpHelper.commonHttpHeaders(), status);
    }
    

    @RequestMapping(
    		value = "/gamelist", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Game>> getGameList(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<Set<Game>>(pem.getGameList(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/game/{gameKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> getGame(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long gameKey,
    		HttpServletRequest request) 
    				throws NumberFormatException, Exception
    {
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<Game>(pem.getGame(gameKey), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/game", 
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Game>> manageGame(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @RequestBody Game game,
    		HttpServletRequest request) 
    				throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	Set<Game> gameSet = pem.manageGame(game);
    	return new ResponseEntity<>(gameSet, HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/person/{personKey}", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getPerson(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @PathVariable Long personKey,
    		HttpServletRequest request) 
    				throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<Person>(pem.getPerson(personKey), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/personlist", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<Person>> getPersonList(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<>(pem.getPersons(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/person/{personKey}/vote", 
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Vote>> vote(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, 
    		@PathVariable Long personKey, @RequestBody Vote vote,
    		HttpServletRequest request) 
    				throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<>(pem.vote(vote, personKey), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/topfivegames", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Game>> getTopFiveGames(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    {
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<>(pem.getTopFiveGames(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/game/{gameKey}/ownership", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<GameOwnership> getGameOwnership(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request, @PathVariable Long gameKey) throws NumberFormatException, Exception
    {
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<GameOwnership>(pem.getGameOwnership(gameKey), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/ownership", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<GameOwnership>> getFullGameOwnership(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    {
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<>(pem.getFullGameOwnership(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }    
    
    @RequestMapping(
    		value = "/account", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Person> getAccount(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<Person>(pem.getAccount(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/steamgames", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Set<SteamGame>> getAllSteamGames(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<Set<SteamGame>>(pem.getFullSteamGameList(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/suggestionlist", 
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Suggestion>> getSuggestionList(
    		@RequestHeader(required = false) HttpHeaders requestHeaders,
    		HttpServletRequest request) throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<List<Suggestion>>(pem.getSuggestions(), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }
    
    @RequestMapping(
    		value = "/suggestion", 
    		method = RequestMethod.POST, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Suggestion> manageSuggestion(
    		@RequestHeader(required = false) HttpHeaders requestHeaders, @RequestBody Suggestion sug,
    		HttpServletRequest request) 
    				throws NumberFormatException, Exception
    { 
    	PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
    	return new ResponseEntity<Suggestion>(pem.manageSuggestion(sug), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
    }

	@RequestMapping(
			value = "/game/{gameKey}/steamappid/{steamAppId}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<Game>> updateGameToSteamApp(
			HttpServletRequest request,
			@PathVariable Long gameKey,
			@PathVariable Long steamAppId) throws Exception
	{
		PreEventManager pem = new PreEventManager(HttpHelper.getUserFromRequest(request));
		return new ResponseEntity<>(pem.setGameToUseSteamGame(gameKey, steamAppId), HttpHelper.commonHttpHeaders(pem.getSessionIdForUser()), HttpStatus.OK);
	}
    
    @Scheduled(fixedDelay = 1000 * 60 * 5)
    public void refreshCache()
    {
    	try
    	{
    		SteamCache.instance().refresh();
    	}
    	catch(Exception e)
    	{
    		// Do Nothing
    	}
    }
}

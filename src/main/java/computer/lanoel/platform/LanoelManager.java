package computer.lanoel.platform;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import computer.lanoel.communication.LANoelAuth;
import computer.lanoel.communication.User;
import computer.lanoel.communication.UserAccount;
import computer.lanoel.contracts.Person;
import computer.lanoel.contracts.Place;
import computer.lanoel.contracts.Tournaments.Lanoel.Round;
import computer.lanoel.contracts.Tournaments.Lanoel.TournamentLanoel;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import computer.lanoel.exceptions.BadRequestException;
import computer.lanoel.exceptions.InvalidSessionException;
import computer.lanoel.platform.database.GameDatabase;
import computer.lanoel.platform.database.PersonDatabase;
import computer.lanoel.platform.database.TournamentLanoelDatabase;
import computer.lanoel.platform.database.VoteDatabase;

public class LanoelManager {

	private UserAccount _user;
	private TournamentLanoelDatabase _lanoelTournDb = new TournamentLanoelDatabase();
	private VoteDatabase _voteDb = new VoteDatabase();
	private GameDatabase _gameDb = new GameDatabase();
	private PersonDatabase _personDb = new PersonDatabase();
	
	public LanoelManager(User user) throws Exception
	{
		getLoggedInUser(user);
		
		if(!LANoelAuth.isAdminUser(_user))
		{
			throw new InvalidSessionException("User is not an admin user", user.getSessionId());
		}
		_lanoelTournDb = new TournamentLanoelDatabase();
	}
	
	private void getLoggedInUser(User user) throws Exception
	{
		UserAccount uAcct = LANoelAuth.loggedInUser(user.getSessionId());
		
		if(uAcct == null)
		{
			throw new InvalidSessionException("User not logged in!", user.getSessionId());
		}
		_user = uAcct;
	}
	
	public String getSessionIdForUser()
	{
		return _user.getUser().getSessionId();
	}
	
	public Long createTournament(String tournamentName) throws Exception
	{
		TournamentLanoel tourn = new TournamentLanoel();
    	tourn.tournamentName = tournamentName;
    	return _lanoelTournDb.insertTournament(tourn);
	}

	public TournamentLanoel manageParticipants(Long tournamentKey, List<TournamentParticipant> participantList) throws SQLException
	{
		for(TournamentParticipant part : participantList)
		{
			if(part.tournamentParticipantKey != null)
			{
				_lanoelTournDb.updateParticipant(part);
			} else
			{
				_lanoelTournDb.addParticipant(tournamentKey, part);
			}
		}
		return _lanoelTournDb.getTournament(tournamentKey);
	}

	public TournamentLanoel removeParticipant(Long tournamentKey, Long participantKey) throws SQLException
	{
		_lanoelTournDb.removeParticipant(participantKey);
		return _lanoelTournDb.getTournament(tournamentKey);
	}
	
	public void createRound(Round round, Long tournamentKey) throws Exception
	{
		if(round.getGame() == null)
    	{
    		throw new BadRequestException("Please provide a game");
    	}
    	
    	if(_gameDb.getGame(round.getGame().getGameKey()) == null)
    	{
    		throw new BadRequestException("Game does not exist");
    	}
    	
    	if(round.getRoundNumber() <= 0)
    	{
    		throw new BadRequestException("Please provide a valid round number");
    	}
    	
    	List<Round> existingRounds = _lanoelTournDb.getRounds(tournamentKey);
    	if(existingRounds.contains(round))
    	{
    		_lanoelTournDb.updateRound(tournamentKey, round);
    	}
    	else
    	{
    		Long roundKey = _lanoelTournDb.insertRound(tournamentKey, round);
			initializeRound(roundKey);
    	}
	}

	private void initializeRound(Long roundKey) throws Exception
	{
		List<Person> personList = _personDb.getPersonList();

		int i = 1;
		for(Person person : personList)
		{
			_lanoelTournDb.insertRoundStanding(person.getPersonKey(), roundKey, i++);
		}
	}
	
	public void recordResult(Long tournamentKey, Long personKey, int roundNumber, int place) throws Exception
	{
    	List<Round> roundList = _lanoelTournDb.getRounds(tournamentKey);
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
    		
    	_lanoelTournDb.insertRoundStanding(personKey, round.getRoundKey(), place);
	}
	
	public void updateScores(Long tournamentKey, int roundNumber, List<Place> places) throws Exception
	{
    	List<Round> roundList = _lanoelTournDb.getTournament(tournamentKey).getRounds();
    	Round tempRound = new Round();
    	tempRound.setRoundNumber(roundNumber);
    	Round round = null;
    	
    	try
    	{
    		round = roundList.get(roundList.indexOf(tempRound));
    	} catch (Exception e)
    	{
    		throw new BadRequestException("Round " + roundNumber + " does not exist");
    	}

		_lanoelTournDb.replaceRoundStandings(round.getRoundKey(), places);
	}
	
	public void resetRoundScores(Long tournamentKey, int roundNumber) throws Exception
	{
		List<Round> roundList = _lanoelTournDb.getTournament(tournamentKey).getRounds();
    	Round tempRound = new Round();
    	tempRound.setRoundNumber(roundNumber);
    	Round round = null;
    	
    	try
    	{
    		round = roundList.get(roundList.indexOf(tempRound));
    	} catch (Exception e)
    	{
    		throw new BadRequestException("Round " + roundNumber + " does not exist");
    	}

		_lanoelTournDb.resetRoundStandings(round.getRoundKey());
	}
	
	public void setPointValues(Map<Integer, Integer> pointMap) throws Exception
	{
		_lanoelTournDb.updatePointValues(pointMap);
	}

	public TournamentLanoel getLanoelTournament(Long tournamentKey) throws SQLException
	{
		return _lanoelTournDb.getTournament(tournamentKey);
	}
}

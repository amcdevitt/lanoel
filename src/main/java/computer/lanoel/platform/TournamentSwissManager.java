package computer.lanoel.platform;

import computer.lanoel.contracts.Tournaments.Swiss.SwissPlayerRound;
import computer.lanoel.contracts.Tournaments.Swiss.SwissRoundResult;
import computer.lanoel.contracts.Tournaments.Swiss.TournamentSwiss;
import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import computer.lanoel.platform.database.TournamentSwissDatabase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by amcde on 4/20/2017.
 */
public class TournamentSwissManager {

    private TournamentSwissDatabase _tournDb = new TournamentSwissDatabase();

    public TournamentSwiss createTournament() throws Exception
    {
        TournamentSwiss tournament = new TournamentSwiss();
        tournament.canPlaySamePlayerMoreThanOnce = false;
        tournament.numberOfRounds = 1;
        tournament.pointsPerGameWon = 3;
        tournament.pointsPerDraw = 1;
        tournament.pointsPerRoundWon = 0;

        return _tournDb.createTournament(tournament);
    }

    public TournamentSwiss getTournamentDetails(Long tournamentKey) throws Exception
    {
        TournamentSwiss t = _tournDb.getTournament(tournamentKey);
        // Set other details like calculated round - upcoming
        t.score = getScoreUpdate(t);
        t.setCurrentRoundPairings(getCurrentRoundPairings(t));
        return t;
    }

    public List<Tournament> getTournamentList() throws Exception
    {
        return _tournDb.getTournamentList();
    }

    public TournamentSwiss updateTournament(TournamentSwiss t) throws Exception
    {
        return _tournDb.updateTournament(t);
    }

    public TournamentSwiss recordRoundResult(Long tournamentKey, SwissRoundResult result) throws Exception
    {
        _tournDb.insertRoundResult(tournamentKey, result);
        return _tournDb.getTournament(tournamentKey);
    }

    public TournamentSwiss addPlayer(String playerName, Long tournamentKey) throws Exception
    {
        TournamentParticipant tp = new TournamentParticipant();
        tp.participantName = playerName;
        _tournDb.addParticipant(tournamentKey, tp);
        return _tournDb.getTournament(tournamentKey);
    }

    public TournamentSwiss removePlayer(Long pKey, Long tounamentKey) throws Exception
    {
        _tournDb.removeParticipantFromTournament(pKey);
        return _tournDb.getTournament(tounamentKey);
    }

    private int getLatestRoundPlayed(TournamentSwiss t)
    {
        final Comparator<SwissPlayerRound> comp = Comparator.comparingInt(r1 -> r1.roundNumber);
        Optional<SwissPlayerRound> latestRound = t.rounds.stream().max(comp);
        return latestRound.isPresent() ? latestRound.get().roundNumber : 0;
    }

    private List<Pair<Long, Long>> getCurrentRoundPairings(TournamentSwiss t) throws Exception
    {
        if(getLatestRoundPlayed(t) >= t.numberOfRounds)
        {
            return new ArrayList<>();
        }
        else
        {
            List<Pair<Long, Long>> pairings = _tournDb.getCurrentRoundPairings(t.tournamentKey);

            if(pairings.size() <= 0)
            {
                pairings = generatePairings(t);
                _tournDb.setCurrentRoundPairings(pairings, t.tournamentKey);
            }

            return pairings;
        }
    }

    public TournamentSwiss setRoundComplete(TournamentSwiss ts) throws Exception
    {
        _tournDb.removeRoundPairings(ts.tournamentKey);
        if(getLatestRoundPlayed(ts) >= ts.numberOfRounds)
        {
            ts.setCurrentRoundPairings(new ArrayList<>());
            return ts;
        }
        List<Pair<Long, Long>> pairings = generatePairings(ts);
        _tournDb.setCurrentRoundPairings(pairings, ts.tournamentKey);
        ts.setCurrentRoundPairings(pairings);
        return ts;
    }

    private List<Pair<Long, Long>> generatePairings(TournamentSwiss ts) throws Exception
    {
        if(ts.participants.isEmpty())
        {
            return new ArrayList<>();
        }

        if(ts.rounds == null || ts.rounds.isEmpty())
        {
            // First round
            List<TournamentParticipant> partList = ts.participants.stream().collect(Collectors.toList());
            Collections.shuffle(partList);
            return getListPairsFromArray(partList.toArray(new TournamentParticipant[0]));
        }

        return getListPairsFromArray(ts.participants.toArray(new TournamentParticipant[0]));
    }

    private List<Pair<Long, Long>> getListPairsFromArray(TournamentParticipant[] array)
    {
        List<Pair<Long, Long>> pairs = new ArrayList<>();
        for(int i = 0; i < array.length - 1; i++)
        {
            pairs.add(new ImmutablePair<>(array[i].tournamentParticipantKey, array[i+1].tournamentParticipantKey));
        }

        if(array.length % 2 != 0)
        {
            pairs.add(new ImmutablePair<>(array[array.length - 1].tournamentParticipantKey, null));
        }

        return pairs;
    }

    private Map<String, Integer> getScoreUpdate(TournamentSwiss ts)
    {
        Map<String, Integer> scoremap = new HashMap<>();

        for(TournamentParticipant part : ts.participants)
        {
            int score = 0;
            Set<SwissPlayerRound> rounds = ts.rounds.stream()
                    .filter(r -> r.playerKey == part.tournamentParticipantKey).collect(Collectors.toSet());
            for(SwissPlayerRound round : rounds)
            {
                score += round.gamesWon * ts.pointsPerGameWon;
                score += round.draws * ts.pointsPerDraw;
                score += round.roundWon ? ts.pointsPerRoundWon : 0;
            }
            scoremap.put(part.participantName, score);
        }

        return scoremap;
    }

}

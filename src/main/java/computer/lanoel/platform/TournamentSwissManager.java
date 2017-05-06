package computer.lanoel.platform;

import computer.lanoel.contracts.Tournaments.Swiss.SwissPlayerRound;
import computer.lanoel.contracts.Tournaments.Swiss.SwissRoundResult;
import computer.lanoel.contracts.Tournaments.Swiss.TournamentSwiss;
import computer.lanoel.contracts.Tournaments.Tournament;
import computer.lanoel.contracts.Tournaments.TournamentParticipant;
import computer.lanoel.platform.database.DatabaseFactory;
import computer.lanoel.platform.database.IDatabase;
import computer.lanoel.platform.database.TournamentSwissDatabase;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by amcde on 4/20/2017.
 */
public class TournamentSwissManager {

    TournamentSwissDatabase db;

    public TournamentSwissManager() throws Exception
    {
        db = (TournamentSwissDatabase) DatabaseFactory.getInstance().getDatabase("TOURNAMENT_SWISS");
    }

    public TournamentSwiss createTournament() throws Exception
    {
        TournamentSwiss tournament = new TournamentSwiss();
        tournament.canPlaySamePlayerMoreThanOnce = false;
        tournament.numberOfRounds = 1;
        tournament.pointsPerGameWon = 3;
        tournament.pointsPerDraw = 1;
        tournament.pointsPerRoundWon = 0;

        try
        {
            return db.createTournament(tournament);
        } finally {
            db.commitAndClose();
        }
    }

    public TournamentSwiss getTournamentDetails(Long tournamentKey) throws Exception
    {
        try {
            TournamentSwiss t = db.getTournament(tournamentKey);
            // Set other details like calculated round - upcoming
            t.score = getScoreUpdate(t);
            t.setCurrentRoundPairings(getCurrentRoundPairings(t));
            return t;
        } finally {
            db.commitAndClose();
        }
    }

    public List<Tournament> getTournamentList() throws Exception
    {
        try {
            return db.getTournamentList();
        } finally {
            db.commitAndClose();
        }
    }

    public TournamentSwiss updateTournament(TournamentSwiss t) throws Exception
    {
        try {
            return db.updateTournament(t);
        } finally {
            db.commitAndClose();
        }
    }

    public TournamentSwiss recordRoundResult(Long tournamentKey, SwissRoundResult result) throws Exception
    {
        try {
            db.insertRoundResult(tournamentKey, result);
            return db.getTournament(tournamentKey);
        } finally {
            db.commitAndClose();
        }
    }

    public TournamentSwiss addPlayer(String playerName, Long tournamentKey) throws Exception
    {
        try {
            TournamentParticipant tp = new TournamentParticipant();
            tp.participantName = playerName;
            db.addParticipant(tournamentKey, tp);
            return db.getTournament(tournamentKey);
        } finally {
            db.commitAndClose();
        }
    }

    public TournamentSwiss removePlayer(Long pKey, Long tounamentKey) throws Exception
    {
        try
        {
            TournamentParticipant p = new TournamentParticipant();
            p.tournamentParticipantKey = pKey;
            db.removeParticipantFromTournament(p);
            return db.getTournament(tounamentKey);
        } finally {
            db.commitAndClose();
        }
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
            List<Pair<Long, Long>> pairings = db.getCurrentRoundPairings(t.tournamentKey);

            if(pairings.size() <= 0)
            {
                pairings = generatePairings(t);
                db.setCurrentRoundPairings(pairings, t.tournamentKey);
            }

            return pairings;
        }
    }

    public TournamentSwiss setRoundComplete(TournamentSwiss ts) throws Exception
    {
        db.removeRoundPairings(ts.tournamentKey);
        if(getLatestRoundPlayed(ts) >= ts.numberOfRounds)
        {
            ts.setCurrentRoundPairings(new ArrayList<>());
            return ts;
        }
        List<Pair<Long, Long>> pairings = generatePairings(ts);
        db.setCurrentRoundPairings(pairings, ts.tournamentKey);
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

        List<TournamentParticipant> orderedPartList = ts.score.entrySet().stream().sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).collect(Collectors.toList());
        return getListPairsFromArray(orderedPartList.toArray(new TournamentParticipant[0]));
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

    private Map<TournamentParticipant, Integer> getScoreUpdate(TournamentSwiss ts)
    {
        Map<TournamentParticipant, Integer> scoremap = new HashMap<>();

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
            scoremap.put(part, score);
        }

        return scoremap;
    }

}

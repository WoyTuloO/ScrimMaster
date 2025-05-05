package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Repositories.MatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final MatchRepository matchRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }


    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(long id) {
        return matchRepository.findById(id);
    }

    public List<Match> getTeamMatches(long teamId){
        return matchRepository.findAll().stream().filter(m -> m.getTeam1().getTeamId() == teamId || m.getTeam2().getTeamId() == teamId).collect(Collectors.toList());
    }


    public Match addMatch(Match match) {
        matchRepository.save(match);
        return match;
    }

    public long deleteMatch(long id) {
        matchRepository.deleteById(id);
        return id;
    }

    public void updateMatch(Match match) {
        Optional<Match> matchOptional = matchRepository.findById(match.getId());

        if (matchOptional.isPresent()) {
            matchOptional.get().setTeam1Score(match.getTeam1Score());
            matchOptional.get().setTeam2Score(match.getTeam2Score());
            matchRepository.save(match);
        }


    }






}

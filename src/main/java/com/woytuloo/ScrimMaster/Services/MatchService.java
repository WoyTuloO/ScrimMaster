package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.MatchDTO;
import com.woytuloo.ScrimMaster.DTO.MatchRequest;
import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.MatchRepository;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {

    @Autowired
    private UserService userService;

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    @Autowired
    public MatchService(MatchRepository matchRepository,
                        TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
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

    public MatchDTO createMatch(MatchRequest req) {
        Team t1 = teamRepository.findById(req.getTeam1Id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team1 not found"));
        Team t2 = teamRepository.findById(req.getTeam2Id())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Team2 not found"));

        Match match = new Match();
        match.getTeams().add(t1);
        match.getTeams().add(t2);
        match.setTeam1Score(req.getTeam1Score());
        match.setTeam2Score(req.getTeam2Score());

        Match saved = matchRepository.save(match);
        return DTOMappers.mapToMatchDTO(saved);
    }


    public Match addMatch(Match match) {
        matchRepository.save(match);
        return match;
    }

    public long deleteMatch(long id) {
        matchRepository.deleteById(id);
        return id;
    }

    public MatchDTO updateMatch(MatchRequest req) {
        Optional<Match> opt = matchRepository.findById(req.getTeam1Id());
        if (opt.isEmpty()) return null;
        Match match = opt.get();
        // jeśli chcesz, możesz też pozwolić na zmianę drużyn
        match.setTeam1Score(req.getTeam1Score());
        match.setTeam2Score(req.getTeam2Score());
        Match updated = matchRepository.save(match);
        return DTOMappers.mapToMatchDTO(updated);
    }


    public List<Match> getUserMatches() {
        Optional<User> currentUser = userService.getCurrentUser();
        return currentUser.map(user -> matchRepository.findByUserName(user.getUsername())).orElseThrow(() -> new RuntimeException("User not found"));
    }
}

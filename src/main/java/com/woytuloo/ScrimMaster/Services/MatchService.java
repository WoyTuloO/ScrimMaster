package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.MatchDTO;
import com.woytuloo.ScrimMaster.DTO.MatchRequest;
import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.PlayerStats;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.MatchRepository;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MatchService {

    private UserService userService;
    private final MatchRepository matchRepository;
    private TeamService teamService;

    @Autowired
    public MatchService(UserService userService, MatchRepository matchRepository, TeamService teamService) {
        this.userService = userService;
        this.matchRepository = matchRepository;
        this.teamService = teamService;
    }

    public Optional<Match> getMatchById(long id) {
        return matchRepository.findById(id);
    }

    @Transactional
    public Match addMatch(Match match) {
        List<PlayerStats> stats1 = match.getTeam1PlayerStats();
        List<PlayerStats> stats2 = match.getTeam2PlayerStats();

        stats1.forEach(userService::updateUserStats);
        stats2.forEach(userService::updateUserStats);

        double avg1 = stats1.stream()
                .mapToDouble(ps -> ps.getPlayer().getRanking())
                .average()
                .orElse(0);
        double avg2 = stats2.stream()
                .mapToDouble(ps -> ps.getPlayer().getRanking())
                .average()
                .orElse(0);

        double expected1 = 1.0 / (1 + Math.pow(10, (avg2 - avg1) / 400));
        double expected2 = 1 - expected1;

        double actual1 = match.getTeam1Score() > match.getTeam2Score() ? 1
                : match.getTeam1Score() < match.getTeam2Score() ? 0
                : 0.5;
        double actual2 = 1 - actual1;

        int K = 30;
        int delta1 = (int)Math.round(K * (actual1 - expected1));
        int delta2 = (int)Math.round(K * (actual2 - expected2));

        stats1.forEach(ps ->
                userService.updateUserRanking(
                        ps.getPlayer().getRanking() + delta1,
                        ps.getPlayer().getId()
                )
        );
        stats2.forEach(ps ->
                userService.updateUserRanking(
                        ps.getPlayer().getRanking() + delta2,
                        ps.getPlayer().getId()
                )
        );

        int team1DeltaTotal = delta1 * stats1.size();
        int team2DeltaTotal = delta2 * stats2.size();

        teamService.getTeamByName(match.getTeam1Name())
                .ifPresent(t -> teamService.updateTeamRanking(t, t.getTeamRanking() + team1DeltaTotal));

        teamService.getTeamByName(match.getTeam2Name())
                .ifPresent(t -> teamService.updateTeamRanking(t, t.getTeamRanking() + team2DeltaTotal));

        return matchRepository.save(match);
    }


    public List<Match> getUserMatches() {
        Optional<User> currentUser = userService.getCurrentUser();
        return currentUser.map(user -> matchRepository.findByUserName(user.getUsername())).orElseThrow(() -> new RuntimeException("User not found"));
    }
}

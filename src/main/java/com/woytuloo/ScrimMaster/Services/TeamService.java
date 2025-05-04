package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByTeamName(name);
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public Team updateTeam(Team team) {
        Optional<Team> optionalTeam = teamRepository.findById(team.getTeamId());
        if(optionalTeam.isPresent()) {
            Team workingTeam = optionalTeam.get();
            workingTeam.setTeamName(team.getTeamName());
            workingTeam.setCaptain(team.getCaptain());
            workingTeam.setPlayers(team.getPlayers());
            workingTeam.setTeamRanking(team.getTeamRanking());
            return teamRepository.save(workingTeam);
        }
        return null;
    }
}

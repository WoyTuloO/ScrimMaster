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

    public List<Team> getTeamByName(String name) {
        return teamRepository.findAll().stream()
                .filter(team -> team.getTeamName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
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
            workingTeam.setPlayer2(team.getPlayer2());
            workingTeam.setPlayer3(team.getPlayer3());
            workingTeam.setPlayer4(team.getPlayer4());
            workingTeam.setPlayer5(team.getPlayer5());
            workingTeam.setPlayer6(team.getPlayer6());
            workingTeam.setPlayer7(team.getPlayer7());
            workingTeam.setTeamRanking(team.getTeamRanking());
            return teamRepository.save(workingTeam);
        }
        return null;
    }
}

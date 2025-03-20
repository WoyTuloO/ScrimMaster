package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TeamService {
    private final TeamRepository teamRepository;

    @Autowired
    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public Team addTeam(Team team) {
        teamRepository.save(team);
        return team;
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }

    public List<Team> getTeamByName(String name) {
        return teamRepository.findAll().stream().filter(team -> team.getTeamname().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
    }

    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public Team updateTeam(Team team) {

        Optional<Team> optionalTeam = teamRepository.findById(team.getId());

        if(optionalTeam.isPresent()) {
            Team workingTeam = optionalTeam.get();
            workingTeam.setTeamname(team.getTeamname());
            workingTeam.setCaptainId(team.getCaptainId());
            workingTeam.setPlayer2Id(team.getPlayer2Id());
            workingTeam.setPlayer3Id(team.getPlayer3Id());
            workingTeam.setPlayer4Id(team.getPlayer4Id());
            workingTeam.setPlayer5Id(team.getPlayer5Id());
            workingTeam.setPlayer6Id(team.getPlayer6Id());
            workingTeam.setPlayer7Id(team.getPlayer7Id());
            workingTeam.setTeamRanking(team.getTeamRanking());
            teamRepository.save(workingTeam);
        }

        return null;

    }
}

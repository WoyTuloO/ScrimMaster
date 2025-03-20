package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/team")
public class TeamController {

    private final TeamService teamService;


    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }


    @GetMapping
    public List<Team> getTeams(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String name) {
        if(teamId != null)
            return (List<Team>) teamService.getTeamById(teamId).get();
        if(name != null)
            return teamService.getTeamByName(name);
        return teamService.getAllTeams();
    }

    @PostMapping
    public ResponseEntity<Team> addTeam(Team team) {
        Team team2 = teamService.addTeam(team);
        return new ResponseEntity<>(team2, HttpStatus.CREATED);
    }
    @PutMapping
    public ResponseEntity<Team> updateTeam(Team team) {
        Team team2 = teamService.updateTeam(team);
        return new ResponseEntity<>(team2, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<>("Team deleted", HttpStatus.OK);
    }


}

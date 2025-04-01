package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.TeamDTO;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/team")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping
    public ResponseEntity<?> getTeams(
            @RequestParam(required = false) Long teamId,
            @RequestParam(required = false) String name) {
        if (teamId != null) {
            Optional<Team> team = teamService.getTeamById(teamId);
            return team.map(value -> new ResponseEntity<>(DTOMappers.mapToTeamDTO(value), HttpStatus.OK))
                    .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
        if (name != null) {
            List<Team> teams = teamService.getTeamByName(name);
            if (teams.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            List<TeamDTO> teamDTOs = teams.stream().map(DTOMappers::mapToTeamDTO).toList();
            return new ResponseEntity<>(teamDTOs, HttpStatus.OK);
        }


        List<Team> teams = teamService.getAllTeams();
        if(teams.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<TeamDTO> teamDTOs = teams.stream().map(DTOMappers::mapToTeamDTO).toList();
        return new ResponseEntity<>(teamDTOs, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Team> addTeam(@RequestBody Team team) {
        Team savedTeam = teamService.addTeam(team);
        return new ResponseEntity<>(savedTeam, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Team> updateTeam(@RequestBody Team team) {
        Team updatedTeam = teamService.updateTeam(team);
        if (updatedTeam != null) {
            return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<>("Team deleted", HttpStatus.OK);
    }
}

package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.TeamDTO;
import com.woytuloo.ScrimMaster.DTO.TeamRequest;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Services.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
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

    @Operation(
            summary = "Pobranie zespołów",
            description = "Zwraca listę zespołów lub pojedynczy zespół. Filtrowanie po `teamId` lub `name`.",
            parameters = {
                    @Parameter(name = "teamId", in = ParameterIn.QUERY, example = "10"),
                    @Parameter(name = "name",   in = ParameterIn.QUERY, example = "Avengers")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zespół / lista zespołów",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = TeamDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono")
            }
    )
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
            Optional<Team> teams = teamService.getTeamByName(name);
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
        List<TeamDTO> teamDTOs = teams.stream().map(DTOMappers::mapToTeamDTO).sorted(Comparator.comparing(TeamDTO::getTeamRanking).reversed()).toList();
        return new ResponseEntity<>(teamDTOs, HttpStatus.OK);
    }

    @Operation(
            summary = "Utwórz zespół",
            description = "Dodaje nowy zespół i zwraca go z nadanym ID.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamRequest.class))
            ),
            responses = @ApiResponse(responseCode = "201",
                    description = "Zespół utworzony",
                    content = @Content(schema = @Schema(implementation = TeamRequest.class)))
    )
    @PostMapping
    public ResponseEntity<TeamDTO> addTeam(@org.springframework.web.bind.annotation.RequestBody TeamRequest req) {
        TeamDTO created = teamService.createTeam(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }


    @Operation(
            summary = "Aktualizuj zespół",
            description = "Modyfikuje dane istniejącego zespołu.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = TeamRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zaktualizowano"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono")
            }
    )
    @PutMapping
    public ResponseEntity<TeamDTO> updateTeam(@org.springframework.web.bind.annotation.RequestBody TeamRequest req) {
        TeamDTO updated = teamService.updateTeam(req);
        if (updated == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Usuń zespół",
            description = "Trwale usuwa zespół o wskazanym ID.",
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, example = "10"),
            responses = @ApiResponse(responseCode = "200", description = "Usunięto")
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return new ResponseEntity<>("Team deleted", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCapitansTeams(@PathVariable Long id) {
        return new ResponseEntity<>(teamService.getCaptainsTeams(id), HttpStatus.OK);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getPlayerTeams() {
        List<TeamDTO> playerTeams = teamService.getPlayerTeams();
        if (playerTeams != null)
            return new ResponseEntity<>(playerTeams, HttpStatus.OK);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

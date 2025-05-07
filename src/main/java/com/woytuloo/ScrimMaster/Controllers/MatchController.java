package com.woytuloo.ScrimMaster.Controllers;


import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Services.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/match")
public class MatchController {
    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @Operation(
            summary = "Lista wszystkich meczów",
            description = "Zwraca pełną listę meczów wraz z danymi zespołów.",
            responses = @ApiResponse(responseCode = "200", description = "Lista meczów",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class))))
    )
    @GetMapping()
    public ResponseEntity<Iterable<Match>> getAllMatches() {
        Iterable<Match> matches = matchService.getAllMatches();
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @Operation(
            summary = "Pobierz mecz po ID",
            description = "Zwraca mecz o danym ID lub 404 jeśli nie istnieje.",
            parameters = @Parameter(name = "matchId", in = ParameterIn.PATH, example = "55"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Znaleziono",
                            content = @Content(schema = @Schema(implementation = Match.class))),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono")
            }
    )
    @GetMapping("{matchId}")
    public ResponseEntity<Match> getMatchById(@PathVariable long matchId) {
        Optional<Match> match = matchService.getMatchById(matchId);
        if (match.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(match.get(), HttpStatus.OK);
    }

    @Operation(
            summary = "Mecze danego zespołu",
            description = "Lista meczów, w których uczestniczy wskazany zespół.",
            parameters = @Parameter(name = "teamId", in = ParameterIn.PATH, example = "10"),
            responses = @ApiResponse(responseCode = "200", description = "Lista meczów",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class))))
    )
    @GetMapping("team/{teamId}")
    public ResponseEntity<Iterable<Match>> getTeamMatches(@PathVariable long teamId) {
        Iterable<Match> matches = matchService.getTeamMatches(teamId);
        return new ResponseEntity<>(matches, HttpStatus.OK);
    }

    @Operation(
            summary = "Dodaj mecz",
            description = "Tworzy nowy mecz między dwoma zespołami.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = Match.class))
            ),
            responses = @ApiResponse(responseCode = "201", description = "Utworzono",
                    content = @Content(schema = @Schema(implementation = Match.class)))
    )
    @PostMapping()
    public ResponseEntity<Match> addMatch(Match match) {
        Match match1 = matchService.addMatch(match);
        return new ResponseEntity<>(match1, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Usuń mecz",
            description = "Usuwa mecz o podanym ID.",
            parameters = @Parameter(name = "matchId", in = ParameterIn.PATH, example = "55"),
            responses = @ApiResponse(responseCode = "200", description = "Usunięto mecz")
    )
    @DeleteMapping("{matchId}")
    public ResponseEntity<String> deleteMatch(@PathVariable int matchId) {
        long l = matchService.deleteMatch(matchId);
        return new ResponseEntity<>("Match deleted", HttpStatus.OK);
    }


    @Operation(
            summary = "Aktualizuj mecz",
            description = "Aktualizuje datę, wynik lub przypisane zespoły istniejącego meczu.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = Match.class))
            ),
            responses = @ApiResponse(responseCode = "200", description = "Zaktualizowano")
    )
    @PutMapping
    public ResponseEntity<Match> updateMatch(@RequestBody Match match) {
        matchService.updateMatch(match);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }





}

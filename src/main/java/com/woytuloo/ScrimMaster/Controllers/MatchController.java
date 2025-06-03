package com.woytuloo.ScrimMaster.Controllers;


import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.MatchDTO;
import com.woytuloo.ScrimMaster.DTO.MatchRequest;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/match")
public class MatchController {
    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
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
    public ResponseEntity<?> getMatchById(@PathVariable long matchId) {
        return matchService.getMatchById(matchId)
                .map(DTOMappers::mapToMatchDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    @Operation(
            summary = "Mecze użytkownika",
            description = "Lista meczów, w których uczestniczy aktualnie zalogowany użytkownik.",
            responses = @ApiResponse(responseCode = "200", description = "Lista meczów",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class))))
    )
    public ResponseEntity<List<MatchDTO>> getUserMatches() {
        try {
            List<Match> matches = matchService.getUserMatches();
            List<MatchDTO> dtos = matches.stream()
                    .map(DTOMappers::mapToMatchDTO)
                    .collect(Collectors.toList());
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }catch(RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

}

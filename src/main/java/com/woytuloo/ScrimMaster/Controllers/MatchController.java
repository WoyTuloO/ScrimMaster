package com.woytuloo.ScrimMaster.Controllers;


import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/match")
public class MatchController {
    private final MatchService matchService;

    @Autowired
    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping()
    public ResponseEntity<Match> addMatch(Match match) {
        Match match1 = matchService.addMatch(match);
        return new ResponseEntity<>(match1, HttpStatus.CREATED);
    }

    @DeleteMapping("{matchId}")
    public ResponseEntity<String> deleteMatch(@PathVariable int matchId) {
        long l = matchService.deleteMatch(matchId);
        return new ResponseEntity<>("Match deleted", HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity<Match> updateMatch(@RequestBody Match match) {
        matchService.updateMatch(match);
        return new ResponseEntity<>(match, HttpStatus.OK);
    }





}

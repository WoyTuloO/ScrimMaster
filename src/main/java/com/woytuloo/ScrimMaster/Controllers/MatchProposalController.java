package com.woytuloo.ScrimMaster.Controllers;


import com.woytuloo.ScrimMaster.DTO.MatchProposalDTO;
import com.woytuloo.ScrimMaster.DTO.MatchProposalRequest;
import com.woytuloo.ScrimMaster.Models.MatchProposal;
import com.woytuloo.ScrimMaster.Models.ProposalStatus;
import com.woytuloo.ScrimMaster.Services.MatchProposalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/match/proposal")
public class MatchProposalController {
    MatchProposalService matchProposalService;

    public MatchProposalController(MatchProposalService matchProposalService) {
        this.matchProposalService = matchProposalService;
    }

    @PostMapping("/submit")
    public ResponseEntity<?> addProposal(@RequestBody MatchProposalRequest request) {

        try{
            ProposalStatus status = matchProposalService.addProposal(request);
            return new ResponseEntity<>(status, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUsersProposals(@PathVariable Long id) {
        List<MatchProposalDTO>  proposalList = matchProposalService.getUsersProposals(id);

        if (proposalList != null)
            return new ResponseEntity<>(proposalList, HttpStatus.OK);
        else
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

package com.woytuloo.ScrimMaster.DTO;

import com.woytuloo.ScrimMaster.Models.ProposalStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class MatchProposalDTO {

    private UUID chatRoomId;
    private int yourScore;
    private int opponentScore;
    private ProposalStatus status;
    private String enemyCaptain;
}

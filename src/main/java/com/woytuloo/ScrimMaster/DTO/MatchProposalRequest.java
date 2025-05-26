package com.woytuloo.ScrimMaster.DTO;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MatchProposalRequest {
    private UUID chatRoomId;
    private String teamName;
    private Long createdBy;
    private int yourScore;
    private int opponentScore;
    private List<PlayerStatsRequest> stats;
}

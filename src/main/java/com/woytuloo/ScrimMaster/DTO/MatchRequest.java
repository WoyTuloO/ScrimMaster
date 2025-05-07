package com.woytuloo.ScrimMaster.DTO;

import lombok.Data;

@Data
public class MatchRequest {
    private Long team1Id;
    private Long team2Id;
    private Integer team1Score;
    private Integer team2Score;
}

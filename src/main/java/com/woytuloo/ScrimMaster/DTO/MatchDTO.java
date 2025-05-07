package com.woytuloo.ScrimMaster.DTO;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class MatchDTO {
    private Long id;
    private TeamDTO team1;
    private TeamDTO team2;
    private int team1Score;
    private int team2Score;
}

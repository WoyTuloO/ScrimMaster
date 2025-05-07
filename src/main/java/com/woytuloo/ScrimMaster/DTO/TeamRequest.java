package com.woytuloo.ScrimMaster.DTO;

import lombok.Data;
import java.util.List;

@Data
public class TeamRequest {
    private String teamName;
    private Long captainId;
    private List<Long> playerIds;
    private Integer teamRanking;
}

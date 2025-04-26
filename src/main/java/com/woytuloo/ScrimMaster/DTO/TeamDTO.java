package com.woytuloo.ScrimMaster.DTO;

import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamDTO {
    private Long teamId;
    private String teamName;
    private UserDTO captain;
    private List<UserDTO> players;
    private Integer teamRanking = 0;
}

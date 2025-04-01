package com.woytuloo.ScrimMaster.DTO;

import com.woytuloo.ScrimMaster.Models.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamDTO {
    private Long teamId;
    private String teamName;
    private UserDTO captain;
    private UserDTO player2;
    private UserDTO player3;
    private UserDTO player4;
    private UserDTO player5;
    private UserDTO player6;
    private UserDTO player7;
    private Integer teamRanking = 0;

}

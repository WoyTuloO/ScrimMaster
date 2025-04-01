package com.woytuloo.ScrimMaster.DTO;

import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;

public class DTOMappers {
    public static TeamDTO mapToTeamDTO(Team team){
        return TeamDTO.builder()
                .teamId(team.getTeamId())
                .teamName(team.getTeamName())
                .captain(mapToUserDTO(team.getCaptain()))
                .player2(mapToUserDTO(team.getPlayer2()))
                .player3(mapToUserDTO(team.getPlayer3()))
                .player4(mapToUserDTO(team.getPlayer4()))
                .player5(mapToUserDTO(team.getPlayer5()))
                .player6(mapToUserDTO(team.getPlayer6()))
                .player7(mapToUserDTO(team.getPlayer7()))
                .teamRanking(team.getTeamRanking())
                .build();
    }

    public static UserDTO mapToUserDTO(User user){
        if (user == null) return null;
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .kd(user.getKd())
                .adr(user.getAdr())
                .ranking(user.getRanking())
                .build();
    }
}

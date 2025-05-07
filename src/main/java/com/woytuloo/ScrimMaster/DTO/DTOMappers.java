package com.woytuloo.ScrimMaster.DTO;

import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;

public class DTOMappers {
    public static TeamDTO mapToTeamDTO(Team team){
        return TeamDTO.builder()
                .teamId(team.getTeamId())
                .teamName(team.getTeamName())
                .captain(mapToUserDTO(team.getCaptain()))
                .players(team.getPlayers().stream()
                        .map(DTOMappers::mapToUserDTO)
                        .toList())
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
                .role(user.getRole())
                .build();
    }

    public static MatchDTO mapToMatchDTO(Match m) {
        return MatchDTO.builder()
                .id(m.getId())
                .team1( mapToTeamDTO(m.getTeam1()) )
                .team2( mapToTeamDTO(m.getTeam2()) )
                .team1Score(m.getTeam1Score())
                .team2Score(m.getTeam2Score())
                .build();
    }


}

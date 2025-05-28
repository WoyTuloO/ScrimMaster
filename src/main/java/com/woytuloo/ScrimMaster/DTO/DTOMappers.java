package com.woytuloo.ScrimMaster.DTO;

import com.woytuloo.ScrimMaster.Models.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

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
                .kd(roundTo2(user.getKd()))
                .adr(roundTo2(user.getAdr()))
                .ranking(user.getRanking())
                .role(user.getRole())
                .build();
    }

    private static double roundTo2(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static PlayerStatsDto toDto(PlayerStats ps) {
        return new PlayerStatsDto(
                ps.getId(),
                ps.getPlayer().getUsername(),
                ps.getTeamSide(),
                ps.getKd(),
                ps.getAdr()
        );
    }

    public static MatchDTO mapToMatchDTO(Match m) {
        List<PlayerStatsDto> team1 = m.getTeam1PlayerStats().stream()
                .map(DTOMappers::toDto)
                .collect(Collectors.toList());

        List<PlayerStatsDto> team2 = m.getTeam2PlayerStats().stream()
                .map(DTOMappers::toDto)
                .collect(Collectors.toList());

        return new MatchDTO(
                m.getId(),
                m.getTeam1Name(),
                m.getTeam1Score(),
                m.getTeam2Name(),
                m.getTeam2Score(),
                team1,
                team2,
                m.getMatchDate()
        );
    }


    public static MatchProposalDTO toProposalDto(MatchProposal p, String enemyCpt) {
        MatchProposalDTO dto = new MatchProposalDTO();
        dto.setChatRoomId(p.getChatRoomId());
        dto.setYourScore(p.getYourScore());
        dto.setOpponentScore(p.getOpponentScore());
        dto.setStatus(p.getStatus());
        dto.setEnemyCaptain(enemyCpt);
        return dto;
    }


}

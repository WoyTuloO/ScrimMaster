package com.woytuloo.ScrimMaster.DTO;

import com.woytuloo.ScrimMaster.Models.*;

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


    public static MatchProposalDTO toDTO(MatchProposal p) {
        MatchProposalDTO d = new MatchProposalDTO();
        d.setId(p.getId());
        d.setTeam1Id(p.getTeam1().getTeamId());
        d.setTeam2Id(p.getTeam2().getTeamId());
        d.setStatus(p.getStatus().name());
        return d;
    }

    public static MatchRequestDTO toRequestDTO(MatchSubmission s) {
        MatchRequestDTO r = new MatchRequestDTO();
        r.setTeamId(s.getTeam().getTeamId());
        r.setFinalScore(s.getFinalScore());
        r.setOpponentScore(s.getOpponentScore());
        r.setStats(s.getStats().stream().map(ps -> {
            PlayerStatDTO psd = new PlayerStatDTO();
            psd.setPlayerId(ps.getPlayer().getId());
            psd.setKd(ps.getKd());
            psd.setAdr(ps.getAdr());
            return psd;
        }).collect(Collectors.toList()));
        return r;
    }

}

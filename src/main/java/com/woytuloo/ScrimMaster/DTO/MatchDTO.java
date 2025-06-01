package com.woytuloo.ScrimMaster.DTO;


import java.util.Date;
import java.util.List;

public record MatchDTO(
        Long id,
        String team1Name,
        int team1Score,
        String team2Name,
        int team2Score,
        List<PlayerStatsDto> team1Stats,
        List<PlayerStatsDto> team2Stats,
        String date
) {}

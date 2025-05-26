package com.woytuloo.ScrimMaster.DTO;

public record PlayerStatsDto(
        Long id,
        String username,
        int teamSide,
        double kd,
        double adr
) {}

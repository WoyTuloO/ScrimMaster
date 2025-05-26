package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.PlayerStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerStatsRepository extends JpaRepository<PlayerStats, Long> {}
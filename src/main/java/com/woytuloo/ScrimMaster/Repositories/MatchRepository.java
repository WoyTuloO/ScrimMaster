package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    Optional<Match> findById(long id);

    void deleteById(long id);

    @Query("""
        SELECT m
        FROM Match m
        WHERE m.team1Name = :teamName OR m.team2Name = :teamName
    """)
    List<Match> findByTeamName(String teamName);

    @Query("""
        SELECT DISTINCT m
        FROM Match m
        JOIN m.team1PlayerStats ps1
        JOIN m.team2PlayerStats ps2
        WHERE ps1.player.username = :username OR ps2.player.username = :username
    """)
    List<Match> findByUserName(String username);
}

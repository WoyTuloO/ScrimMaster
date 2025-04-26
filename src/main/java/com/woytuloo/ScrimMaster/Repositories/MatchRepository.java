package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    public Optional<Match> findById(long id);
    public void deleteById(long id);

    @Query("""
        SELECT DISTINCT m
        FROM Match m
        JOIN m.teams t
        WHERE t.teamName = :teamName
    """)
    public List<Match> findByTeamName(String teamName);

    @Query("""
        SELECT DISTINCT m
        FROM Match m
        JOIN m.teams t
        JOIN t.players p
        WHERE p.username = :username
    """)
    public List<Match> findByUserName(String username);
}

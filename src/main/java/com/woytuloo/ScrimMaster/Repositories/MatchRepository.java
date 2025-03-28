package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {
    public Optional<Match> findById(long id);

    Match getMatchById(Long id);
}

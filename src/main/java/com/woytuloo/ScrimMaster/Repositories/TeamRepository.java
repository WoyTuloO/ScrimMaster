package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    public void deleteById(long id);
    public Optional<Team> findByTeamName(String teamName);
    public Optional<Team> findById(long id);
}

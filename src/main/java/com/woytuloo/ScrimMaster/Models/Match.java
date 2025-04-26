package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "match_teams",
            joinColumns = @JoinColumn(name = "match_id"),
            inverseJoinColumns = @JoinColumn(name = "team_id")
    )
    private List<Team> teams = new ArrayList<>();

    private int team1Score;
    private int team2Score;

    public Match() {}

    public Match(Team team1, Team team2) {
        this.teams.add(team1);
        this.teams.add(team2);
        this.team1Score = 0;
        this.team2Score = 0;
    }

    public Team getTeam1() {
        if (teams.size() > 0) {
            return teams.get(0);
        }
        return null;
    }

    public Team getTeam2() {
        if (teams.size() > 1) {
            return teams.get(1);
        }
        return null;
    }
}

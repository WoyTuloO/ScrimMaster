package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String team1Name;
    private int    team1Score;

    private String team2Name;
    private int    team2Score;

    private String  matchDate;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "team_side = 1")
    private List<PlayerStats> team1PlayerStats = new ArrayList<>();

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true)
    @Where(clause = "team_side = 2")
    private List<PlayerStats> team2PlayerStats = new ArrayList<>();

    public Match() {}

    public Match(String team1Name,
                 String team2Name,
                 int team1Score,
                 int team2Score,
                 List<PlayerStats> stats1,
                 List<PlayerStats> stats2)
    {
        this.team1Name = team1Name;
        this.team2Name = team2Name;
        this.team1Score = team1Score;
        this.team2Score = team2Score;

        this.team1PlayerStats.addAll(stats1);
        this.team2PlayerStats.addAll(stats2);

        matchDate = LocalDate.now().toString();
    }
}

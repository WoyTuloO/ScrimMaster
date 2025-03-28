package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;

import static java.lang.String.valueOf;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private Long id;

    private Long team1Id;
    private Long team2Id;

    private int team1Score;
    private int team2Score;

    public Long getId() {
        return id;
    }

    public Long getTeam1Id() {
        return team1Id;
    }
    public Long getTeam2Id() {
        return team2Id;
    }

    public Match() {}

    public Match(Long team1Id, Long team2Id) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.team1Score = 0;
        this.team2Score = 0;
    }

    public Integer getT1Score() {
        return team1Score;
    }
    public Integer getT2Score() {
        return team2Score;
    }

    public void setT1Score(Integer t1Score) {
        this.team1Score = t1Score;
    }

    public void setT2Score(Integer t2Score) {
        this.team2Score = t2Score;
    }


}

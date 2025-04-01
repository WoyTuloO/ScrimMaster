package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;

import static java.lang.String.valueOf;

@Data
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

    public Match() {}

    public Match(Long team1Id, Long team2Id) {
        this.team1Id = team1Id;
        this.team2Id = team2Id;
        this.team1Score = 0;
        this.team2Score = 0;
    }


}

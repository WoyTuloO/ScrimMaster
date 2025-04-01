package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long teamId;

    @Column(unique = true, nullable = false)
    private String teamName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id", nullable = false)
    private User captain;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private User player2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player3_id")
    private User player3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player4_id")
    private User player4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player5_id")
    private User player5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player6_id")
    private User player6;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player7_id")
    private User player7;

    private Integer teamRanking = 0;

    public Team() {}

    public Team(String teamName, User captain) {
        this.teamName = teamName;
        this.captain = captain;
    }

}

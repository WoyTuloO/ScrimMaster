package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id", unique = true, nullable = false)
    private Long id;

    private Long team1Id;
    private Long team2Id;


    public Match() {}

}

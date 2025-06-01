package com.woytuloo.ScrimMaster.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "player_stats")
public class PlayerStats {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "match_id", nullable = true)
    @JsonIgnore
    private Match match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", nullable = true)
    @JsonIgnore
    private MatchProposal proposal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private User player;

    @Column(name = "team_side", nullable = false)
    private int teamSide;

    private double kd;
    private double adr;
}

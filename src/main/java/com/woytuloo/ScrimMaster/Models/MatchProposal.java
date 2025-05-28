package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "match_proposals")
public class MatchProposal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID chatRoomId;

    @Column(nullable = false)
    private int yourScore;

    @Column(nullable = false)
    private int opponentScore;

    @Column(nullable = false)
    private String teamName;

    @Column(nullable = false)
    private long createdBy;

    @Column(nullable = false)
    private ProposalStatus status;

    @OneToMany(mappedBy = "proposal", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlayerStats> stats = new ArrayList<>();
}

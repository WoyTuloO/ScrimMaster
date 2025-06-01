package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class TeamInvitation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User invitedUser;

    @ManyToOne(optional = false)
    private User invitedBy; // kapitan

    @ManyToOne(optional = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;

    private LocalDateTime sentAt = LocalDateTime.now();

}


package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "public_messages")
@Getter @Setter @NoArgsConstructor
public class PublicMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;
    private Instant timestamp;

    public PublicMessage(String sender, String content, Instant timestamp) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
    }
}

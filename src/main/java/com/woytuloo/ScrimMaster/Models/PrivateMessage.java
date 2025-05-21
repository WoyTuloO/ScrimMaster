package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "private_messages")
@Getter @Setter @NoArgsConstructor
public class PrivateMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    private String sender;
    private String content;
    private Instant timestamp;

    public PrivateMessage(ChatRoom room, String sender, String content, Instant timestamp) {
        this.chatRoom = room;
        this.sender   = sender;
        this.content  = content;
        this.timestamp= timestamp;
    }
}
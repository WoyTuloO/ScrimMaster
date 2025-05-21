package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "chat_rooms")
@Data
public class ChatRoom {
    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String userA;

    @Column(nullable = false)
    private String userB;

    public ChatRoom(String id, String userA, String userB) {
        this.id = id;
        this.userA = userA;
        this.userB = userB;
    }

    public ChatRoom() {
    }
}

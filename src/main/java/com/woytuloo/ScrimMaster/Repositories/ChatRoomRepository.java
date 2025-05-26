package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {
    void removeById(String id);
    ChatRoom findChatRoomById(String id);

    List<ChatRoom> findChatRoomsByUserAOrUserB(String userA, String userB);
}
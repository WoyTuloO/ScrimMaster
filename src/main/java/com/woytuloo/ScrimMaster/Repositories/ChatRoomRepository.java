package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> { }
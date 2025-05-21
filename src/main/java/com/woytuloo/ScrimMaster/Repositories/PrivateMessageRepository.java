// src/main/java/com/woytuloo/ScrimMaster/Repositories/PrivateMessageRepository.java
package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.PrivateMessage;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface PrivateMessageRepository extends JpaRepository<PrivateMessage, Long> {

    @Query("""
      SELECT pm FROM PrivateMessage pm
       WHERE pm.chatRoom.id = :roomId
       ORDER BY pm.timestamp DESC
      """)
    List<PrivateMessage> findLatestByRoom(@Param("roomId") String roomId, Limit limit);

}

package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.PublicMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface PublicMessageRepository extends JpaRepository<PublicMessage, Long> {

    @Query(value = "SELECT * FROM public_messages ORDER BY timestamp DESC LIMIT :limit", nativeQuery = true)
    List<PublicMessage> findLatest(@Param("limit") int limit);
}

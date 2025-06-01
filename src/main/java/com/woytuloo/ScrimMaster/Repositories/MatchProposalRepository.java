package com.woytuloo.ScrimMaster.Repositories;


import com.woytuloo.ScrimMaster.Models.MatchProposal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MatchProposalRepository extends JpaRepository<MatchProposal, Long> {
    MatchProposal findByChatRoomId(UUID chatRoomId);
    boolean existsByChatRoomId(UUID chatRoomId);
    void removeAllByChatRoomId(UUID chatRoomId);

    List<MatchProposal> findAllByChatRoomId(UUID chatRoomId);
    List<MatchProposal> findAllByCreatedBy(Long createdBy);
}

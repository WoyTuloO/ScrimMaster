package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.InvitationStatus;
import com.woytuloo.ScrimMaster.Models.TeamInvitation;
import com.woytuloo.ScrimMaster.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeamInvitationRepository extends JpaRepository<TeamInvitation, Long> {
    List<TeamInvitation> findByInvitedUserAndStatus(User user, InvitationStatus status);
}

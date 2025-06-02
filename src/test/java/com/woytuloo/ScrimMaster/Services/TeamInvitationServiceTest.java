package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.*;
import com.woytuloo.ScrimMaster.Repositories.TeamInvitationRepository;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class TeamInvitationServiceTest {

    @Mock private TeamInvitationRepository invitationRepo;
    @Mock private UserRepository userRepo;
    @Mock private TeamRepository teamRepo;
    @Mock private UserService userService;
    @InjectMocks private TeamInvitationService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void getPendingInvitationsForUser_noUser_throws() {
        when(userService.getCurrentUser()).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.getPendingInvitationsForUser());
    }
}

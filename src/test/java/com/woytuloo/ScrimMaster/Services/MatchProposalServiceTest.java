package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.MatchProposalRequest;
import com.woytuloo.ScrimMaster.DTO.PlayerStatsRequest;
import com.woytuloo.ScrimMaster.Models.*;
import com.woytuloo.ScrimMaster.Repositories.ChatRoomRepository;
import com.woytuloo.ScrimMaster.Repositories.MatchProposalRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MatchProposalServiceTest {

    @Mock MatchProposalRepository proposalRepo;
    @Mock MatchService matchService;
    @Mock UserRepository userRepo;
    @Mock ChatRoomRepository chatRoomRepo;
    @InjectMocks MatchProposalService service;

    @BeforeEach void setUp() { MockitoAnnotations.openMocks(this); }

    @Test void addProposal_firstProposal_savesPending() {
        MatchProposalRequest req = new MatchProposalRequest();
        req.setChatRoomId(UUID.randomUUID());
        req.setTeamName("teamA");
        req.setCreatedBy(1L);
        req.setYourScore(12);
        req.setOpponentScore(9);
        req.setStats(List.of());
        when(proposalRepo.existsByChatRoomId(any())).thenReturn(false);
        assertThat(service.addProposal(req)).isEqualTo(ProposalStatus.Pending);
        verify(proposalRepo).save(any());
    }

    @Test void addProposal_replaceSameUser() {
        MatchProposalRequest req = new MatchProposalRequest();
        req.setChatRoomId(UUID.randomUUID());
        req.setTeamName("teamA");
        req.setCreatedBy(1L);
        req.setYourScore(12);
        req.setOpponentScore(9);
        req.setStats(List.of());
        MatchProposal prev = new MatchProposal();
        prev.setCreatedBy(1L);
        prev.setStats(new ArrayList<>());
        when(proposalRepo.existsByChatRoomId(any())).thenReturn(true);
        when(proposalRepo.findByChatRoomId(any())).thenReturn(prev);
        assertThat(service.addProposal(req)).isEqualTo(ProposalStatus.Pending);
    }

    @Test void addProposal_equalScores_finalizeMatch() {
        MatchProposalRequest req = new MatchProposalRequest();
        req.setChatRoomId(UUID.randomUUID());
        req.setTeamName("teamA");
        req.setCreatedBy(1L);
        req.setYourScore(10);
        req.setOpponentScore(9);
        req.setStats(List.of());
        MatchProposal prev = new MatchProposal();
        prev.setCreatedBy(2L);
        prev.setStats(new ArrayList<>());
        prev.setOpponentScore(10);
        prev.setYourScore(9);
        when(proposalRepo.existsByChatRoomId(any())).thenReturn(true);
        when(proposalRepo.findByChatRoomId(any())).thenReturn(prev);
        doNothing().when(proposalRepo).removeAllByChatRoomId(any());
        when(chatRoomRepo.findChatRoomById(anyString())).thenReturn(new ChatRoom("id","a","b"));
        assertThat(service.addProposal(req)).isEqualTo(ProposalStatus.Pending);
    }

    @Test void addProposal_differentScores_reject() {
        MatchProposalRequest req = new MatchProposalRequest();
        req.setChatRoomId(UUID.randomUUID());
        req.setTeamName("teamA");
        req.setCreatedBy(1L);
        req.setYourScore(8);
        req.setOpponentScore(8);
        req.setStats(List.of());
        MatchProposal prev = new MatchProposal();
        prev.setCreatedBy(2L);
        prev.setStats(new ArrayList<>());
        prev.setOpponentScore(9);
        prev.setYourScore(10);
        when(proposalRepo.existsByChatRoomId(any())).thenReturn(true);
        when(proposalRepo.findByChatRoomId(any())).thenReturn(prev);
        when(chatRoomRepo.findChatRoomById(anyString())).thenReturn(new ChatRoom("id","a","b"));
        doNothing().when(proposalRepo).removeAllByChatRoomId(any());
        assertThat(service.addProposal(req)).isEqualTo(ProposalStatus.Rejected);
    }

    @Test void removeProposals_callsRepo() {
        UUID id = UUID.randomUUID();
        service.removeProposals(id);
        verify(proposalRepo).removeAllByChatRoomId(id);
    }

    @Test void rejectProposals_setsRoomRejected() {
        UUID id = UUID.randomUUID();
        ChatRoom room = new ChatRoom("id","a","b");
        when(chatRoomRepo.findChatRoomById(anyString())).thenReturn(room);
        ProposalStatus res = service.rejectProposals(id);
        assertThat(res).isEqualTo(ProposalStatus.Rejected);
        assertThat(room.getStatus()).isEqualTo("Rejected");
    }

    @Test void replaceProposal_replacesStatsAndScores() {
        MatchProposal prev = new MatchProposal();
        prev.setStats(new ArrayList<>());
        prev.setCreatedBy(1L);
        MatchProposal next = new MatchProposal();
        next.setStats(new ArrayList<>());
        next.setYourScore(7); next.setOpponentScore(6); next.setTeamName("t");
        when(proposalRepo.findByChatRoomId(any())).thenReturn(prev);
        service.replaceProposal(next);
        verify(proposalRepo).save(prev);
    }

    @Test void toEntity_throwsIfUserNotFound() {
        MatchProposalRequest req = new MatchProposalRequest();
        req.setStats(List.of(new PlayerStatsRequest()));
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.toEntity(req));
    }

    @Test void getUsersProposals_ok() {
        User u = new User("xx", "a", "mail@x");
        u.setId(7L);
        when(userRepo.findById(7L)).thenReturn(Optional.of(u));
        when(chatRoomRepo.findChatRoomById(anyString())).thenReturn(new ChatRoom("id","a","b"));
        when(proposalRepo.findAllByCreatedBy(7L)).thenReturn(List.of(new MatchProposal()));
        assertThat(service.getUsersProposals(7L)).isNotNull();
    }

    @Test void getUsersProposals_nullIfNotFound() {
        when(userRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThat(service.getUsersProposals(1L)).isNull();
    }
}

package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.MatchProposalDTO;
import com.woytuloo.ScrimMaster.DTO.MatchProposalRequest;
import com.woytuloo.ScrimMaster.DTO.PlayerStatsRequest;
import com.woytuloo.ScrimMaster.Models.*;
import com.woytuloo.ScrimMaster.Repositories.ChatRoomRepository;
import com.woytuloo.ScrimMaster.Repositories.MatchProposalRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MatchProposalService {

    private final MatchProposalRepository proposalRepo;
    private final MatchService matchService;
    private final UserRepository userRepo;
    private final ChatRoomRepository chatRoomRepo;

    public MatchProposalService(MatchProposalRepository repo, MatchService matchService, UserRepository userRepo, ChatRoomRepository chatRoomRepo) {
        this.proposalRepo = repo;
        this.matchService = matchService;
        this.userRepo = userRepo;
        this.chatRoomRepo = chatRoomRepo;
    }

    public ProposalStatus addProposal(MatchProposalRequest req) {

        MatchProposal proposal = toEntity(req);
        proposal.setStatus(ProposalStatus.Pending);

        if(!proposalRepo.existsByChatRoomId(proposal.getChatRoomId())) {
            proposalRepo.save(proposal);
            return ProposalStatus.Pending;
        }

        MatchProposal prop2 = proposalRepo.findByChatRoomId(proposal.getChatRoomId());

        if(prop2.getCreatedBy() == proposal.getCreatedBy()) {
            replaceProposal(proposal);
            return ProposalStatus.Pending;
        }

        if(prop2.getOpponentScore() == proposal.getYourScore() && prop2.getYourScore() == proposal.getOpponentScore()) {
            finalizeMatchProposal(proposal, prop2);
            removeProposals(proposal.getChatRoomId());
            return ProposalStatus.Pending;
        }

        return rejectProposals(proposal.getChatRoomId());
    }

    public void removeProposals(UUID chatRoomId){
        proposalRepo.removeAllByChatRoomId(chatRoomId);
    }

    public ProposalStatus rejectProposals(UUID chatRoomId){
        removeProposals(chatRoomId);

        ChatRoom chatRoomById = chatRoomRepo.findChatRoomById(String.valueOf(chatRoomId));
        chatRoomById.setStatus("Rejected");
        chatRoomRepo.save(chatRoomById);

        return ProposalStatus.Rejected;
    }

    public void replaceProposal(MatchProposal newProposal) {
        MatchProposal prev = proposalRepo.findByChatRoomId(newProposal.getChatRoomId());

        for (PlayerStats ps : prev.getStats()) {
            ps.setProposal(null);
        }
        prev.getStats().clear();

        for (PlayerStats ps : newProposal.getStats()) {
            ps.setProposal(prev);
            prev.getStats().add(ps);
        }

        prev.setOpponentScore(newProposal.getOpponentScore());
        prev.setYourScore(newProposal.getYourScore());
        prev.setTeamName(newProposal.getTeamName());

        proposalRepo.save(prev);
    }


    public void finalizeMatchProposal(MatchProposal proposal, MatchProposal prop2) {
        Match match = new Match(
                proposal.getTeamName(), prop2.getTeamName(),
                proposal.getYourScore(), proposal.getOpponentScore(),
                proposal.getStats(), prop2.getStats()
        );

        for (PlayerStats ps : match.getTeam1PlayerStats()) {
            ps.setMatch(match);
            ps.setTeamSide(1);
            ps.setProposal(null);
        }
        for (PlayerStats ps : match.getTeam2PlayerStats()) {
            ps.setMatch(match);
            ps.setTeamSide(2);
            ps.setProposal(null);
        }

        matchService.addMatch(match);
        ChatRoom chatRoomById = chatRoomRepo.findChatRoomById(String.valueOf(proposal.getChatRoomId()));
        chatRoomById.setStatus("Closed");
        chatRoomRepo.save(chatRoomById);
    }

    public MatchProposal toEntity(MatchProposalRequest dto) {
        MatchProposal proposal = new MatchProposal();

        proposal.setChatRoomId(dto.getChatRoomId());
        proposal.setTeamName(dto.getTeamName());
        proposal.setCreatedBy(dto.getCreatedBy());
        proposal.setYourScore(dto.getYourScore());
        proposal.setOpponentScore(dto.getOpponentScore());

        List<PlayerStats> statsList = new ArrayList<>();
        for (PlayerStatsRequest statDto : dto.getStats()) {
            User user = userRepo.findByUsername(statDto.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found: " + statDto.getUsername()));

            PlayerStats stat = new PlayerStats();
            stat.setPlayer(user);
            stat.setKd(statDto.getKd());
            stat.setAdr(statDto.getAdr());
            stat.setProposal(proposal);

            statsList.add(stat);
        }

        proposal.setStats(statsList);
        return proposal;
    }


    public List<MatchProposalDTO> getUsersProposals(Long id) {
        Optional<User> byId = userRepo.findById(id);
        return byId.map(user -> proposalRepo.findAllByCreatedBy(id).stream().map(p -> {
            ChatRoom byChatRoomId = chatRoomRepo.findChatRoomById(String.valueOf(p.getChatRoomId()));
            if (!byChatRoomId.getUserA().equals(user.getUsername()))
                return DTOMappers.toProposalDto(p, byChatRoomId.getUserA());
            else
                return DTOMappers.toProposalDto(p, byChatRoomId.getUserB());
        }).toList()).orElse(null);

    }
}

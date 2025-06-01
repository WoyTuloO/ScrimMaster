package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.TeamDTO;
import com.woytuloo.ScrimMaster.DTO.TeamRequest;
import com.woytuloo.ScrimMaster.Models.Team;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.TeamRepository;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.hibernate.annotations.NotFound;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TeamService {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final TeamInvitationService teamInvitationService;

    @Autowired
    public TeamService(TeamRepository teamRepository, UserRepository userRepository, UserService userService, TeamInvitationService teamInvitationService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.userService = userService;
        this.teamInvitationService = teamInvitationService;
    }

    public Team addTeam(Team team) {
        return teamRepository.save(team);
    }
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }
    public Optional<Team> getTeamById(Long id) {
        return teamRepository.findById(id);
    }
    public Optional<Team> getTeamByName(String name) {
        return teamRepository.findByTeamName(name);
    }
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }

    public Team updateTeam(Team team) {
        Optional<Team> optionalTeam = teamRepository.findById(team.getTeamId());
        if(optionalTeam.isPresent()) {
            Team workingTeam = optionalTeam.get();
            workingTeam.setTeamName(team.getTeamName());
            workingTeam.setCaptain(team.getCaptain());
            workingTeam.setPlayers(team.getPlayers());
            workingTeam.setTeamRanking(team.getTeamRanking());
            return teamRepository.save(workingTeam);
        }
        return null;
    }

//    public TeamDTO createTeam(TeamRequest req) {
//        User captain = userRepository.findById(req.getCaptainId())
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Captain not found"));
//        List<User> players = userRepository.findAllById(req.getPlayerIds());
//        if (players.stream().noneMatch(u -> u.getId().equals(captain.getId()))) {
//            players.add(captain);
//        }
//
//        Team team = new Team();
//        team.setTeamName(req.getTeamName());
//        team.setCaptain(captain);
//        team.setPlayers(players);
//        team.setTeamRanking(req.getTeamRanking());
//        Team saved = teamRepository.save(team);
//
//        return DTOMappers.mapToTeamDTO(saved);
//    }

    public TeamDTO createTeam(TeamRequest req) {
        User captain = userRepository.findById(req.getCaptainId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Captain not found"));

        Team team = new Team();
        team.setTeamName(req.getTeamName());
        team.setCaptain(captain);
        team.setPlayers(new ArrayList<>(List.of(captain)));
        team.setTeamRanking(1000);
        Team saved = teamRepository.save(team);

        List<Long> playerIdsToInvite = req.getPlayerIds().stream()
                .filter(id -> !id.equals(captain.getId()))
                .toList();
        teamInvitationService.inviteUsersToTeam(playerIdsToInvite, saved, captain);

        return DTOMappers.mapToTeamDTO(saved);
    }


    public TeamDTO updateTeam(TeamRequest req) {
        Optional<Team> opt = teamRepository.findById(req.getCaptainId());
        if (opt.isEmpty()) return null;
        Team team = opt.get();
        team.setTeamName(req.getTeamName());
        User captain = userRepository.findById(req.getCaptainId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Captain not found"));
        team.setCaptain(captain);

        List<User> players = userRepository.findAllById(req.getPlayerIds());
        if (players.stream().noneMatch(u -> u.getId().equals(captain.getId()))) {
            players.add(captain);
        }
        team.setPlayers(players);
        team.setTeamRanking(req.getTeamRanking());

        Team updated = teamRepository.save(team);
        return DTOMappers.mapToTeamDTO(updated);
    }

    public List<TeamDTO> getCaptainsTeams(Long id) {
        User cpt = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Captain not found"));
        return teamRepository.findAllByCaptain_Username(cpt.getUsername()).stream().map(DTOMappers::mapToTeamDTO).collect(Collectors.toList());
    }

    public void updateTeamRanking(Team t, int i) {
        t.setTeamRanking(i);
        teamRepository.save(t);
    }


    public List<TeamDTO> getPlayerTeams() {
        Optional<User> currentUser = userService.getCurrentUser();
        if(currentUser.isPresent())
            return teamRepository.findAllByPlayers_Username(currentUser.get().getUsername())
                    .stream().map(DTOMappers::mapToTeamDTO).toList();

        return null;
    }

}

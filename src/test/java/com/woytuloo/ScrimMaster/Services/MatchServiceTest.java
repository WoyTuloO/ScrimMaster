package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.Match;
import com.woytuloo.ScrimMaster.Repositories.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    private MatchService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MatchService(matchRepository);
    }

    @Test
    void shallGetAllMatches() {
        underTest.getAllMatches();
        verify(matchRepository).findAll();
    }

    @Test
    void shallGetMatchById() {
        long id = 1L;
        underTest.getMatchById(id);
        verify(matchRepository).findById(id);
    }

    @Test
    void shallGetTeamMatches() {
        long teamId = 1L;
        underTest.getTeamMatches(teamId);
        verify(matchRepository).findAll();
    }

    @Test
    void shallAddMatch() {
        Match match = new Match();

        underTest.addMatch(match);
        verify(matchRepository).save(match);
    }

    @Test
    void shallDeleteMatch() {
        long id = 1L;
        underTest.deleteMatch(id);
        verify(matchRepository).deleteById(id);
    }

    @Test
    void shallUpdateMatch() {
        Match match = new Match();
        match.setId(1L);
        match.setTeam1Score(10);
        match.setTeam2Score(20);

        given(matchRepository.findById(match.getId())).willReturn(Optional.of(match));

        underTest.updateMatch(match);
        verify(matchRepository).findById(match.getId());
        verify(matchRepository).save(match);
    }

    @Test
    void shallUpdateMatch_matchIsNotPresent() {
        Match match = new Match();
        match.setId(1L);
        match.setTeam1Score(10);
        match.setTeam2Score(20);

        underTest.updateMatch(match);
        verify(matchRepository).findById(match.getId());
        then(matchRepository).should(never()).save(any());
    }



}

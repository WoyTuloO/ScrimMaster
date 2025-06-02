package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.MatchProposal;
import com.woytuloo.ScrimMaster.Models.PlayerStats;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MatchProposalRepositoryTest {

    @Autowired
    private MatchProposalRepository matchProposalRepository;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password");
    static {
        postgres.start();
    }
    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry reg) {
        reg.add("spring.datasource.url", postgres::getJdbcUrl);
        reg.add("spring.datasource.username", postgres::getUsername);
        reg.add("spring.datasource.password", postgres::getPassword);
    }

    private UUID chatRoomId;

    @BeforeEach
    void setUp() {
        chatRoomId = UUID.randomUUID();
        MatchProposal proposal = new MatchProposal();
        proposal.setChatRoomId(chatRoomId);
        proposal.setTeamName("PropoTeam");
        proposal.setCreatedBy(1L);
        proposal.setYourScore(16);
        proposal.setOpponentScore(14);
        proposal.setStatus(com.woytuloo.ScrimMaster.Models.ProposalStatus.Pending);
        matchProposalRepository.save(proposal);
    }

    @AfterEach
    void tearDown() {
        matchProposalRepository.deleteAll();
    }

    @Test
    void findByChatRoomId() {
        MatchProposal found = matchProposalRepository.findByChatRoomId(chatRoomId);
        assertThat(found).isNotNull();
        assertThat(found.getTeamName()).isEqualTo("PropoTeam");
    }

    @Test
    void existsByChatRoomId() {
        boolean exists = matchProposalRepository.existsByChatRoomId(chatRoomId);
        assertThat(exists).isTrue();
        boolean notExists = matchProposalRepository.existsByChatRoomId(UUID.randomUUID());
        assertThat(notExists).isFalse();
    }

    @Test
    void removeAllByChatRoomId() {
        matchProposalRepository.removeAllByChatRoomId(chatRoomId);
        assertThat(matchProposalRepository.findByChatRoomId(chatRoomId)).isNull();
    }

    @Test
    void findAllByCreatedBy() {
        List<MatchProposal> list = matchProposalRepository.findAllByCreatedBy(1L);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getTeamName()).isEqualTo("PropoTeam");
    }
}

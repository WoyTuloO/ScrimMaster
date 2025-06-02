package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.RefreshToken;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock private RefreshTokenRepository repo;
    @InjectMocks private RefreshTokenService service;

    @BeforeEach
    void setUp() { MockitoAnnotations.openMocks(this); }

    @Test
    void findByToken_found() {
        RefreshToken t = new RefreshToken();
        when(repo.findByToken("abc")).thenReturn(Optional.of(t));
        assertThat(service.findByToken("abc")).isPresent();
    }

    @Test
    void verifyExpiration_valid() {
        RefreshToken t = new RefreshToken();
        t.setExpiryDate(Instant.now().plusSeconds(1000));
        assertThat(service.verifyExpiration(t)).isEqualTo(t);
    }

    @Test
    void verifyExpiration_expired() {
        RefreshToken t = new RefreshToken();
        t.setExpiryDate(Instant.now().minusSeconds(100));
        assertThrows(RuntimeException.class, () -> service.verifyExpiration(t));
    }

    @Test
    void deleteByUser_callsRepo() {
        User u = new User("x","x","x@x");
        service.deleteByUser(u);
        verify(repo).deleteByUser(u);
    }
}

package com.woytuloo.ScrimMaster.Services;

import com.woytuloo.ScrimMaster.Models.RefreshToken;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository repo;
    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshDurationMs;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    public RefreshToken createRefreshToken(User user) {
        repo.deleteByUser(user);
        repo.flush();
        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusMillis(refreshDurationMs));
        token.setToken(UUID.randomUUID().toString());
        return repo.save(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            repo.delete(token);
            throw new RuntimeException("Refresh token wygasł");
        }
        return token;
    }

    public void deleteByUser(User user) {
        repo.deleteByUser(user);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return repo.findByToken(token);
    }
}
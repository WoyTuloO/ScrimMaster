package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.RefreshToken;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Security.JwtUtils;
import com.woytuloo.ScrimMaster.Services.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshSvc;
    private final UserRepository userRepo;

    public AuthController(AuthenticationManager authManager,
                          JwtUtils jwtUtils,
                          RefreshTokenService refreshSvc,
                          UserRepository userRepo) {
        this.authManager = authManager;
        this.jwtUtils = jwtUtils;
        this.refreshSvc = refreshSvc;
        this.userRepo = userRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User creds, HttpServletResponse res) {

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepo.findByUsername(creds.getUsername())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie istnieje"));
        List<String> roles = List.of(user.getPersmissionLevel() == 1 ? "ADMIN" : "USER");

        String access = jwtUtils.generateAccessToken(user.getUsername(), roles);
        RefreshToken refresh = refreshSvc.createRefreshToken(user);

        res.addHeader("Set-Cookie", buildAccessCookie(access).toString());
        res.addHeader("Set-Cookie", buildRefreshCookie(refresh.getToken()).toString());

        return ResponseEntity.ok("Zalogowano");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String rt,
                                          HttpServletResponse res) {

        RefreshToken stored = refreshSvc.verifyExpiration(
                refreshSvc.findByToken(rt)
                        .orElseThrow(() -> new RuntimeException("Brak refresh tokena")));

        User user = stored.getUser();
        List<String> roles = List.of(user.getPersmissionLevel() == 1 ? "ADMIN" : "USER");
        String newAccess = jwtUtils.generateAccessToken(user.getUsername(), roles);

        res.addHeader("Set-Cookie", buildAccessCookie(newAccess).toString());
        return ResponseEntity.ok("Token odświeżony");
    }

    @GetMapping("/me")
    public ResponseEntity<String> me(Authentication auth) {
        return ResponseEntity.ok(auth.getName());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken", required = false) String rt,
            Authentication auth, HttpServletResponse res) {

        if (rt != null) {
            refreshSvc.findByToken(rt).ifPresent(rf -> refreshSvc.deleteByUser(rf.getUser()));
        } else if (auth != null) {
            userRepo.findByUsername(auth.getName())
                    .ifPresent(refreshSvc::deleteByUser);
        }

        clearCookie(res, "accessToken", "/", true);
        clearCookie(res, "accessToken", "/", false);

        clearCookie(res, "refreshToken", "/api/auth", true);
        clearCookie(res, "refreshToken", "/api/auth", false);
        clearCookie(res, "refreshToken", "/api/auth/refresh", true); // legacy
        clearCookie(res, "refreshToken", "/api/auth/refresh", false);

        return ResponseEntity.ok("Wylogowano");
    }


    private ResponseCookie buildAccessCookie(String value) {
        return ResponseCookie.from("accessToken", value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(jwtUtils.getAccessExpirationMs() / 1000)
                .build();
    }

    private ResponseCookie buildRefreshCookie(String value) {
        return ResponseCookie.from("refreshToken", value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/api/auth")
                .maxAge(jwtUtils.getRefreshExpirationMs() / 1000)
                .build();
    }

    private void clearCookie(HttpServletResponse res, String name, String path, boolean secure) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .sameSite("None")
                .path(path)
                .maxAge(0)
                .build();

        res.addHeader("Set-Cookie", cookie.toString());
    }
}

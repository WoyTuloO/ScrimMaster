package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.RefreshToken;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Security.JwtUtils;
import com.woytuloo.ScrimMaster.Services.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
@CrossOrigin(origins = "*")
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

    @Schema(description = "Dane logowania")
    public record LoginRequest(
            @Schema(description = "Nazwa użytkownika", example = "demo")    String username,
            @Schema(description = "Hasło użytkownika",  example = "secret") String password
    ) {}

    @Operation(
            summary = "Logowanie i wydanie pary tokenów",
            description = "Przyjmuje nazwę użytkownika i hasło, weryfikuje je i zwraca parę (access + refresh) "
                    + "w httpOnly cookies. Access token jest w formacie JWT z rolami, refresh trafia do bazy.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Dane logowania (username + password)",
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Przykład",
                                    value = """
                        { "username": "demo", "password": "secret" }
                        """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zalogowano – tokeny w cookies"),
                    @ApiResponse(responseCode = "401", description = "Błędne dane uwierzytelniające")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<?> login(@org.springframework.web.bind.annotation.RequestBody User creds, HttpServletResponse res) {

        Authentication auth = authManager.authenticate(new UsernamePasswordAuthenticationToken(creds.getUsername(), creds.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);

        User user = userRepo.findByUsername(creds.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        List<String> roles = List.of(user.getRole());

        String access = jwtUtils.generateAccessToken(user.getUsername(), roles);
        RefreshToken refresh = refreshSvc.createRefreshToken(user);

        res.addHeader("Set-Cookie", buildAccessCookie(access).toString());
        res.addHeader("Set-Cookie", buildRefreshCookie(refresh.getToken()).toString());

        return ResponseEntity.ok("Zalogowano");
    }

    @Operation(
            summary = "Odśwież access token",
            description = "Przyjmuje refresh token z cookie, sprawdza jego ważność i generuje nowy access token "
                    + "bez zmiany refresha. Nowy access trafia do httpOnly cookie.",
            parameters = @Parameter(name = "refreshToken", in = ParameterIn.COOKIE,
                    required = true, description = "Refresh token"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Nowy access token wygenerowany"),
                    @ApiResponse(responseCode = "401", description = "Refresh token nieprawidłowy lub wygasł")
            }
    )
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue("refreshToken") String rt,
                                          HttpServletResponse res) {

        RefreshToken stored = refreshSvc.verifyExpiration(
                refreshSvc.findByToken(rt)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Brak refresh tokena")));

        User user = stored.getUser();
        List<String> roles = List.of(user.getRole());
        String newAccess = jwtUtils.generateAccessToken(user.getUsername(), roles);

        res.addHeader("Set-Cookie", buildAccessCookie(newAccess).toString());
        return ResponseEntity.ok("Token odświeżony");
    }


    @Operation(
            summary = "Wylogowanie (kasuje tokeny i cookie)",
            security = @SecurityRequirement(name = "bearer-jwt"),
            parameters = @Parameter(name = "refreshToken", in = ParameterIn.COOKIE,
                    required = false, description = "Opcjonalny refresh token"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Wylogowano pomyślnie")
            }
    )
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
        clearCookie(res, "refreshToken", "/api/auth/refresh", true);
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

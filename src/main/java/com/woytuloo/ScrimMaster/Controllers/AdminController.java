package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.UserDTO;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import com.woytuloo.ScrimMaster.Services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final UserRepository userRepo;

    public AdminController(UserService userService, UserRepository userRepo) {
        this.userService = userService;
        this.userRepo = userRepo;
    }

    @Operation(
            summary = "Pobierz wszystkich użytkowników (panel admina)",
            description = "Zwraca listę wszystkich użytkowników w systemie. Wymaga roli ADMIN.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista użytkowników",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
                    @ApiResponse(responseCode = "403", description = "Brak wymaganych uprawnień")
            }
    )
    @GetMapping("/users")
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(DTOMappers::mapToUserDTO)
                .toList();
    }

    @Operation(
            summary = "Usuń użytkownika",
            description = "Usuwa użytkownika o podanym ID. Wymaga roli ADMIN.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Użytkownik usunięty"),
                    @ApiResponse(responseCode = "401", description = "Brak autoryzacji"),
                    @ApiResponse(responseCode = "403", description = "Brak wymaganych uprawnień"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono użytkownika")
            }
    )
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}

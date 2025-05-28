package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.DTO.DTOMappers;
import com.woytuloo.ScrimMaster.DTO.UserDTO;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.*;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path ="api/user")
@Tag(name = "Users", description = "Operacje na użytkownikach")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(
            summary = "Lista użytkowników lub wyszukiwanie",
            description = """
        Zwraca wszystkich użytkowników, albo – jeśli podasz filtr – tylko tych spełniających warunki.
        Filtry wzajemnie się wykluczają, kolejność priorytetów: id -> username -> email.
        """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Znaleziono użytkowników",
                            content = @Content(array = @ArraySchema(
                                    schema = @Schema(implementation = UserDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono pasujących rekordów"),
            }
    )
    @Parameters({
            @Parameter(name = "id",      in = ParameterIn.QUERY, description = "Id użytkownika",    example = "42"),
            @Parameter(name = "username",in = ParameterIn.QUERY, description = "Nazwa użytkownika", example = "woytuloo"),
            @Parameter(name = "email",   in = ParameterIn.QUERY, description = "Adres e-mail",      example = "a@b.com")
    })
    @GetMapping()
    public ResponseEntity<?> getUsers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ){
         if(id != null){
             Optional<User> userById = userService.getUserById(id);
             if(userById.isPresent())
                 return new ResponseEntity<>(userById.stream().map(DTOMappers::mapToUserDTO).toList(), HttpStatus.OK);
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         } else if(username != null){
            return new ResponseEntity<>(userService.getUsersByName(username), HttpStatus.OK);
        } else if(email != null){
             return new ResponseEntity<>(userService.getUsersByEmail(email), HttpStatus.OK);
        }

        List<User> allUsers = userService.getAllUsers();
         if(allUsers.isEmpty())
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>( allUsers, HttpStatus.OK);

    }

    @Operation(
            summary = "Bieżący zalogowany użytkownik",
            description = "Zwraca DTO użytkownika powiązanego z aktualnym JWT. Jeśli brak tokena – HTTP 401.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zalogowany użytkownik",
                            content = @Content(schema = @Schema(implementation = UserDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Brak autoryzacji")
            }
    )
    @GetMapping("/currentUser")
    public ResponseEntity<?> getCurrentUser() {
        Optional<User> userOpt = userService.getCurrentUser();
        if (userOpt.isPresent()) {
            return new ResponseEntity<>(DTOMappers.mapToUserDTO(userOpt.get()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }



    @Operation(
            summary = "Utwórz użytkownika (rejestracja)",
            description = "Dodaje nowego użytkownika do bazy. Zwraca zapisany obiekt z nadanym ID.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            responses = @ApiResponse(responseCode = "201",
                    description = "Użytkownik utworzony",
                    content = @Content(schema = @Schema(implementation = User.class)))
    )
    @PostMapping
    public ResponseEntity<User> addUser(@org.springframework.web.bind.annotation.RequestBody User user){
        System.out.println(user.toString());
        User createdUser = userService.addUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Aktualizuj użytkownika",
            description = "Modyfikuje istniejącego użytkownika na podstawie ID w ciele żądania.",
            requestBody = @RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Zaktualizowano"),
                    @ApiResponse(responseCode = "404", description = "Nie znaleziono")
            }
    )
    @PutMapping("")
    public ResponseEntity<User> updateUser(@org.springframework.web.bind.annotation.RequestBody User user){
        User toUpdateUser = userService.updateUser(user);
        if (toUpdateUser != null) {
            return new ResponseEntity<>(toUpdateUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


    @Operation(
            summary = "Usuń użytkownika",
            description = "Usuwa użytkownika o podanym ID.",
            parameters = @Parameter(name = "userId", in = ParameterIn.PATH, example = "42"),
            responses = @ApiResponse(responseCode = "200", description = "Usunięto")
    )
    @Transactional
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if(userService.deleteUser(id))
            return ResponseEntity.ok().build();

        return ResponseEntity.notFound().build();
    }






}

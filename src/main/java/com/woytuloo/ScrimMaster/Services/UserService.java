package com.woytuloo.ScrimMaster.Services;


import com.woytuloo.ScrimMaster.Models.PlayerStats;
import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll().stream().filter(u-> !u.getUsername().equals("User Deleted")).sorted(Comparator.comparing(User::getRanking).reversed()).collect(Collectors.toList());
    }

    public List<User> getUsersByName(String name) {
        return new ArrayList<>(userRepository.findByUsernameContainingIgnoreCase(name));
    }

    public List<User> getUsersByEmail(String email) {
        return new ArrayList<>(userRepository.findByEmailContainingIgnoreCase(email));
    }

    public void deleteUserByUsername(String name) {
        userRepository.deleteByUsername(name);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public User addUser(User user) {

        if(userRepository.findByUsername(user.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Użytkownik o takim nicku już istnieje");
        }
        if(userRepository.findByEmail(user.getEmail()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Użytkownik o takim adresie email już istnieje");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRanking(1000);
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> oldUser = userRepository.findById(user.getId());

        if (oldUser.isPresent()) {
            User workingUser = oldUser.get();
            workingUser.setPassword(user.getPassword());
            workingUser.setRole(user.getRole());
            workingUser.setAdr(user.getAdr());
            workingUser.setKd(user.getKd());
            workingUser.setRanking(user.getRanking());

            userRepository.save(oldUser.get());
            return workingUser;
        }
        return null;
    }


    public Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
                    return Optional.empty();
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }


    public void updateUserStats(PlayerStats playerStat) {

        Optional<User> byId = userRepository.findById(playerStat.getPlayer().getId());
        if (byId.isPresent()) {
            User user = byId.get();
            double newKd = user.getKd() == 0 ? playerStat.getKd(): (user.getKd() + playerStat.getKd())/2;
            double newAdr = user.getAdr() == 0 ? playerStat.getAdr() : (user.getAdr() + playerStat.getAdr())/2;

            user.setAdr(newAdr);
            user.setKd(newKd) ;
            userRepository.save(user);
        }

    }

    public void updateUserRanking(int v, Long id) {
        Optional<User> byId = userRepository.findById(id);
        if (byId.isPresent()) {
            User user = byId.get();
            user.setRanking(v);
            userRepository.save(user);
        }

    }

    public boolean deleteUser(Long id){
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
//            user.setDeleted(true);
            user.setUsername("User Deleted");
            user.setEmail("deleted_" + user.getId() + "@example.com");
            userRepository.save(user);
            return true;
        }
        return false;
    }


    public ResponseEntity<?> changeNickname( String newUsername){
        Optional<User> userOpt = getCurrentUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Nie jesteś zalogowany"));
        }
        User user = userOpt.get();

        if (newUsername == null || newUsername.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nick nie może być pusty"));
        }
        if (userRepository.existsByUsername(newUsername)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nick jest już zajęty"));
        }
        user.setUsername(newUsername);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<?> changePassword(String currentPassword, String newPassword){

        Optional<User> userOpt = getCurrentUser();
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Nie jesteś zalogowany"));
        }
        User user = userOpt.get();


        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Obecne hasło jest nieprawidłowe"));
        }
        if (newPassword == null || newPassword.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("message", "Nowe hasło jest za krótkie"));
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return ResponseEntity.ok().build();


    }
}

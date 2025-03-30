package com.woytuloo.ScrimMaster.Services;


import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getUsersByName(String name) {
        return userRepository.findAll().stream().filter(user -> user.getUsername().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
    }

    public List<User> getUserByEmail(String email) {
        return userRepository.findAll().stream().filter(user -> user.getEmail().toLowerCase().contains(email.toLowerCase())).collect(Collectors.toList());
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
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        Optional<User> oldUser = userRepository.findById(user.getId());

        if (oldUser.isPresent()) {
            User workingUser = oldUser.get();
            workingUser.setPassword(user.getPassword());
            workingUser.setPersmissionLevel(user.getPersmissionLevel());
            workingUser.setAdr(user.getAdr());
            workingUser.setKd(user.getKd());
            workingUser.setRanking(user.getRanking());

            userRepository.save(oldUser.get());
            return workingUser;
        }
        return null;
    }
}

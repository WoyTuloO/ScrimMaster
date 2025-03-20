package com.woytuloo.ScrimMaster.Controllers;

import com.woytuloo.ScrimMaster.Models.User;
import com.woytuloo.ScrimMaster.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path ="api/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<User>> getUsers(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String email
    ){
         if(id != null){
             Optional<User> userById = userService.getUserById(id);
             if(userById.isPresent())
                 return new ResponseEntity<>(userById.stream().toList(), HttpStatus.OK);
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);
         } else if(username != null){
            return new ResponseEntity<>(userService.getUsersByName(username), HttpStatus.OK);
        } else if(email != null){
             return new ResponseEntity<>(userService.getUserByEmail(email), HttpStatus.OK);
        }

        List<User> allUsers = userService.getAllUsers();
         if(allUsers.isEmpty())
             return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>( allUsers, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user){
        User createdUser = userService.addUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping("")
    public ResponseEntity<User> updateUser(@RequestBody User user){
        User toUpdateUser = userService.updateUser(user);
        if (toUpdateUser != null) {
            return new ResponseEntity<>(toUpdateUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    @DeleteMapping("{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId){
        userService.deleteUserById(userId);
        return new ResponseEntity<>("Successfully deleted user", HttpStatus.OK);
    }






}

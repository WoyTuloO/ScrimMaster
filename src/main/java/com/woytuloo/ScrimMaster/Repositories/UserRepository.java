package com.woytuloo.ScrimMaster.Repositories;

import com.woytuloo.ScrimMaster.Models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    void deleteByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameContainingIgnoreCase(String username);
    Optional<User> findByEmailContainingIgnoreCase(String email);


}

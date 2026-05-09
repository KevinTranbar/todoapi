package com.example.todoapi.repository;

import com.example.todoapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username); //Username in name = Look for the "username" column of the database, String username in param = The value to compare that column against
    //Optional = might or might not have value
    boolean existsByUsername(String username);
    //boolean = returns true for found and false for not found
}

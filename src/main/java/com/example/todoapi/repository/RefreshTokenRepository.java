package com.example.todoapi.repository;

import com.example.todoapi.model.RefreshToken;
import com.example.todoapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user); //DELETE * FROM refresh_tokens WHERE user_id(name parsing) = ?(value from parameter value)
}
//TLDR: name parsing looks at refreshToken entity and finds the User field --> sees ManyToOne annotation --> Knows to look at User's PK = ...WHERE user_id = ? //(User user) provides the actual user object(use users PK in WHERE clause)
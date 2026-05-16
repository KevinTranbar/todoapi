package com.example.todoapi.service;

import com.example.todoapi.exception.InvalidCredentialsException;
import com.example.todoapi.model.RefreshToken;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.RefreshTokenRepository;
import org.springframework.transaction.annotation.Transactional; //Use when method has more than one operation to same DB (All or nothing)
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh-expiration}") long refreshExpiration) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpiration = refreshExpiration;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString()); //Not secret key because it doesn't need to be self-verifiable, only unique (UUID.randomUUID() generates random unique identifier)
        refreshToken.setUser(user);
        refreshToken.setExpiresAt(new Date(System.currentTimeMillis() + refreshExpiration));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken validateRefreshToken(String token) { //Takes in token part from refreshToken entity
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token) //Finds full refreshToken entity from DB with token part
                .orElseThrow(() -> new InvalidCredentialsException());

        if (refreshToken.isRevoked()) {
            throw new InvalidCredentialsException();
        }

        if (refreshToken.getExpiresAt().before(new Date())) {
            throw new InvalidCredentialsException();
        }

        return refreshToken; //Takes in only token part of entity, returns full entity based on token part
    }
    //Used when access token expires
    @Transactional //Ensures that old token is revoked and new token is created (Transactional = Either all or none succeeds)
    public RefreshToken rotateRefreshToken(RefreshToken oldRefToken) { //Rotate refresh token for user //Takes in full refreshToken entity
        oldRefToken.setRevoked(true); //Mark old token as revoked
        refreshTokenRepository.save(oldRefToken); //Save the revoked old token to DB, JPA realizes that oldToken Id is same as existing token in DB, UPDATE instead of INSERT
        return createRefreshToken(oldRefToken.getUser()); //Give new refresh token to user with old token's user
    }
    //Used on login and logout
    @Transactional //Added for convention, and safety
    public void removeAllUserRefreshTokens(User user) { //Removes all users refresh tokens
        refreshTokenRepository.deleteByUser(user);
    }
}

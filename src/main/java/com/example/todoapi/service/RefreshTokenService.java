package com.example.todoapi.service;

import com.example.todoapi.exception.InvalidCredentialsException;
import com.example.todoapi.model.RefreshToken;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private Long refreshExpiration;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${jwt.refresh.expiration}") Long refreshExpiration) {
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

    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidCredentialsException());

        if (refreshToken.isRevoked()) {
            throw new InvalidCredentialsException();
        }

        if (refreshToken.getExpiresAt().before(new Date())) {
            throw new InvalidCredentialsException();
        }

        return refreshToken;
    }
    //Used when access token expires
    @Transactional //Ensures that old token is revoked and new token is created (Transactional = Either all or none succeeds)
    public RefreshToken rotateRefreshToken(RefreshToken oldToken) { //Rotate refresh token for user
        oldToken.setRevoked(true); //Mark old token as revoked
        refreshTokenRepository.save(oldToken); //Save the revoked old token to DB, JPA realizes that oldToken Id is same as existing token in DB, UPDATE instead of INSERT
        return createRefreshToken(oldToken.getUser()); //Give new refresh token to user with old token's user
    }
    //Used on login and logout
    @Transactional //Added for convention, and safety
    public void removeAllUserRefreshTokens(User user) { //Removes all users refresh tokens
        refreshTokenRepository.deleteByUser(user);
    }
}

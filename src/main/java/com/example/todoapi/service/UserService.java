package com.example.todoapi.service;

import com.example.todoapi.dto.AuthResponse;
import com.example.todoapi.dto.RegisterRequest;
import com.example.todoapi.dto.LoginRequest;
import com.example.todoapi.exception.InvalidCredentialsException;
import com.example.todoapi.exception.UsernameAlreadyTakenException;
import com.example.todoapi.model.RefreshToken;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; //Use when method has more than one operation to same DB (All or nothing)

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UsernameAlreadyTakenException(request.getUsername());
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        String accessToken = jwtService.generateToken(request.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken()); //accessToken = Just a string, refreshToken = Object with token inside --> need to extract token from object
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }
        
        refreshTokenService.removeAllUserRefreshTokens(user); //Clears all users refresh tokens before creating new one to prevent old ones from being used when improper logout (different browser, close tab, etc)
                                                              //Logout prerequisite login, but login doesn't prerequisite logout (Which is why we clear here and in logout)
        String accessToken = jwtService.generateToken(request.getUsername());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @Transactional
    public AuthResponse refresh(String token) { //Takes in token part from refreshToken entity
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(token); //Validate token part of refreshToken entity, return full entity based on token part
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken); //Revoke refreshToken entity in DB and create new one - return
        String newAccessToken = jwtService.generateToken(newRefreshToken.getUser().getUsername()); //Generate new access token with either full refreshToken entity

        return new AuthResponse(newAccessToken, newRefreshToken.getToken()); //Return new access token using username from newRefreshToken entity (Doesn't matter which one)
    }

    public void logout(String token) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(token); //Kinda over-engineered, but serves purpose no matter how small - User could for example logout with a random string (doesn't really matter, but good to prevent)
        refreshTokenService.removeAllUserRefreshTokens(refreshToken.getUser()); //Clear users refresh tokens
    }

    public User getCurrentUser() { //Only way to get the current user. JWT token effectively gone after security passes --> Only way to get user details is through security context(which carries on whole req), where userDetails is stored
        String username = SecurityContextHolder.getContext()
                .getAuthentication() //Stores user details
                .getName(); //Extract username from userDetails

        return userRepository.findByUsername(username) //Return user object
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}

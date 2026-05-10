package com.example.todoapi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService { //Handles JWT logic: Generate, validate, extract username

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret, //jwt.secret injected from application.properties into String secret here (Value injection, like bean injection)
            @Value("${jwt.expiration}") long expiration) {
                this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)); //Convert String secret into byte array (Cryptographic algo works with byte arrays) using UTF-8 encoding. Wrap in Keys.hmacShaKeyFor() to create a SecretKey
                this.expiration = expiration;
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username) //Who token for
                .issuedAt(new Date()) //When token was issued
                .expiration(new Date(System.currentTimeMillis() + expiration)) //When token expires
                .signWith(secretKey) //Sign with secretKey
                .compact(); //Build final JWT string
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject(); //Given a JWT string, extract username
    }

    public boolean isTokenValid(String token) { //Validate token
        try {
            getClaims(token); //If no exception thrown, token is valid
            return true;
        } catch (Exception e) { //getClaims() throws exception if token is invalid
            return false;
        }
    }

    //Private helper method - Shared logic between generateToken() and isTokenValid()
    private Claims getClaims(String token) { //Claims = interface that represents the JWT payload
        return Jwts.parser() //JWT parser = Object that reads and validates JWT strings
                .verifyWith(secretKey) //Verify signature
                .build() //Build validation object
                .parseSignedClaims(token) //Given a token: Splits token (header, payload, signature), compares re-computed signature with signature in token and checks expiration (valid or not valid) (Also unscrambles the JWT string)
                .getPayload(); //If valid, return payload
    }
}

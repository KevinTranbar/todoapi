package com.example.todoapi.model;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "refresh_tokens") //Stores refresh tokens server-side, hybrid instead of stateless, why?
public class RefreshToken {     //Revocation = If user logs out --> invalidate their refresh token. Next time someone tries to use it --> Reject
                                //Rotation = Every time refresh token used, generate new one and invalidate old one
    @Id                         //Multiple devices = User might be logged in on phone, tablet, laptop, etc. Each device has own refresh token
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; //One user can have many refresh tokens

    @Column(nullable = false)
    private Date expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    public RefreshToken() {

    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }
    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }
    public void setRevoked(boolean revoked) {
        this.revoked = revoked;
    }
}

package com.example.todoapi.dto;

import jakarta.validation.constraints.NotBlank;

public class RefreshRequest { //Represents only the token part of the refreshToken entity

    @NotBlank
    private String refreshToken;

    public RefreshRequest() {

    }

    public RefreshRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

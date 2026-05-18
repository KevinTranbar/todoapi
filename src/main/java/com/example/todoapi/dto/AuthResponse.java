package com.example.todoapi.dto;

public class AuthResponse { //Exists for a clean response with JWT token/Refresh token and convention.
    //Why not send back token as is? You can, but it wont be as a JSON object, it will be a string (not wanted), dto's wraps in JSON object (wanted) (look TodoResponse for context)
    //Why so simple? Right now we only send back token, later might add when expires, etc

    private String accessToken;
    private String refreshToken;

    public AuthResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return accessToken;
    }
    public void setToken(String token) {
        this.accessToken = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

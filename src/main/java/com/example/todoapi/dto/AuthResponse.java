package com.example.todoapi.dto;

public class AuthResponse { //Exists for a clean response with JWT token/Refresh token and convention.
    //Why not send back token as is? You can, but it wont be as a JSON object, it will be a string (not wanted), dto's wraps in JSON object (wanted) (look TodoResponse for context)
    //Why so simple? Right now we only send back token, later might add when expires, etc

    private String token;
    private String refreshToken;

    public AuthResponse(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}

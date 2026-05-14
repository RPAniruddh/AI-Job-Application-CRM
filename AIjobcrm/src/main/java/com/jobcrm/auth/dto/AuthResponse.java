package com.jobcrm.auth.dto;

public class AuthResponse {

    private String token;
    private String email;
    private String fullName;

    // Builder pattern manually implemented
    private AuthResponse() {}

    public static Builder builder() { return new Builder(); }

    public String getToken() { return token; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }

    public static class Builder {
        private final AuthResponse instance = new AuthResponse();

        public Builder token(String token) { instance.token = token; return this; }
        public Builder email(String email) { instance.email = email; return this; }
        public Builder fullName(String fullName) { instance.fullName = fullName; return this; }
        public AuthResponse build() { return instance; }
    }
}
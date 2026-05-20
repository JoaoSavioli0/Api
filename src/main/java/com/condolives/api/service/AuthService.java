package com.condolives.api.service;

import com.condolives.api.dto.auth.CreateAccountRequest;
import com.condolives.api.dto.auth.LoginRequest;
import com.condolives.api.dto.auth.LoginResponse;
import com.condolives.api.dto.auth.RegisterRequest;

public interface AuthService {
    void createAccount(CreateAccountRequest request);
    LoginResponse login(LoginRequest request);
    void logout(String token);
    LoginResponse register(RegisterRequest request);
}

package com.scaler.userserviceapr21capstone.services;

import com.scaler.userserviceapr21capstone.models.Token;
import com.scaler.userserviceapr21capstone.models.User;
import org.springframework.stereotype.Service;

public interface UserService
{
    User signup(String name, String email, String password);
    Token login(String email, String password);
    void logout(Token token);
    User validateToken(String tokenValue);
}

package com.scaler.userserviceapr21capstone.controllers;

import com.scaler.userserviceapr21capstone.dtos.*;
import com.scaler.userserviceapr21capstone.models.Token;
import com.scaler.userserviceapr21capstone.models.User;
import com.scaler.userserviceapr21capstone.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController
{
    UserService userService;
    public UserController(UserService userService)
    {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public UserDto signup(@RequestBody SignUpRequestDto signUpRequestDto)
    {
        User user = userService.signup(signUpRequestDto.getName(),
                signUpRequestDto.getEmail(), signUpRequestDto.getPassword());

        return UserDto.from(user);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto loginRequestDto)
    {
        Token token = userService.login(loginRequestDto.getEmail(), loginRequestDto.getPassword());
        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setTokenValue(token.getTokenValue());
        return loginResponseDto;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto logoutRequestDto)
    {
        return null;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token)
    {
        if(token.startsWith("Bearer "))
        {
            token = token.replace("Bearer ", "");
        }

        User user = userService.validateToken(token);
        ResponseEntity<Boolean> responseEntity;

        if(user == null)
        {
            responseEntity = new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
        else
        {
            responseEntity = new ResponseEntity<>(true, HttpStatus.OK);
        }

        return responseEntity;
    }
}

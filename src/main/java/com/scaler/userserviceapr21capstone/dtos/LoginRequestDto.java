package com.scaler.userserviceapr21capstone.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto
{
    private String email;
    private String password;
}

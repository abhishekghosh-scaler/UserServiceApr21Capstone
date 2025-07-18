package com.scaler.userserviceapr21capstone.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
public class Token extends Base
{
    private String tokenValue;
    private Date expiryAt;

    @ManyToOne
    private User user;
}

/*
* User ------- Token
*  1 ----------- M
*  1 ----------- 1
* User:Token = 1:M
* */

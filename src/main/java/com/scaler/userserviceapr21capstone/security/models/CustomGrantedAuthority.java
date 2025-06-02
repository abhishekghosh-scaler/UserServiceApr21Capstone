package com.scaler.userserviceapr21capstone.security.models;

import com.scaler.userserviceapr21capstone.models.Role;
import org.springframework.security.core.GrantedAuthority;

public class CustomGrantedAuthority implements GrantedAuthority
{
    private final String authority;

    public CustomGrantedAuthority(Role role)
    {
        this.authority = role.getValue();
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}

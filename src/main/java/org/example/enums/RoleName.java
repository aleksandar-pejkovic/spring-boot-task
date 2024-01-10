package org.example.enums;

import org.springframework.security.core.GrantedAuthority;

public enum RoleName implements GrantedAuthority {

    USER,
    ADMIN;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}

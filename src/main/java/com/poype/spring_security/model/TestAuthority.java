package com.poype.spring_security.model;

import org.springframework.security.core.GrantedAuthority;

public class TestAuthority implements GrantedAuthority {

    private final String authority;

    public TestAuthority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}

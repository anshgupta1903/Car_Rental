package com.example.demo.model;

public enum Role {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_MANAGER("MANAGER");

    private final String authority;

    Role(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
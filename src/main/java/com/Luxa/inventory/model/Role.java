package com.Luxa.inventory.model;

public enum Role {
    ADMIN,
    VIEWER;

    public String authority() {
        return "ROLE_" + name();
    }
}

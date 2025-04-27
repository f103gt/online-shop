package com.internetshop.model;

public enum Role {
    CUSTOMER,
    ADMIN;

    public static Role fromString(String role) throws IllegalArgumentException {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }

        try {
            return Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid role: '%s'. Valid values are: %s",
                            role,
                            String.join(", ", getValidRoles()))
            );
        }
    }

    private static String[] getValidRoles() {
        Role[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}
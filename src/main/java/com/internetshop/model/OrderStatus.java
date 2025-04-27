package com.internetshop.model;

public enum OrderStatus {
    PAYED,
    PENDING;

    public static OrderStatus fromString(String status) throws IllegalArgumentException {
        if (status == null) {
            throw new IllegalArgumentException("Order status cannot be null");
        }

        try {
            return OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("Invalid order status: '%s'. Valid values are: %s",
                            status,
                            String.join(", ", getValidStatuses()))
            );
        }
    }

    private static String[] getValidStatuses() {
        OrderStatus[] values = values();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }
}

package com.internetshop.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public class Order {
    private final int id;
    private final int userId;
    private final LocalDateTime orderDate;
    private final LocalDateTime receivalDate;
    private BigDecimal totalAmount;
    private final OrderStatus status;

    // Private constructor for builder
    private Order(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.orderDate = builder.orderDate;
        this.receivalDate = builder.receivalDate;
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public Optional<LocalDateTime> getReceivalDate() {
        return Optional.ofNullable(receivalDate);
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    // Builder class
    public static class Builder {
        private int id;
        private int userId;
        private LocalDateTime orderDate;
        private LocalDateTime receivalDate;
        private BigDecimal totalAmount;
        private OrderStatus status;

        public Builder() {}

        public Builder id(int id) {
            this.id = id;
            return this;
        }

        public Builder userId(int userId) {
            this.userId = userId;
            return this;
        }

        public Builder orderDate(LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        public Builder receivalDate(LocalDateTime receivalDate) {
            this.receivalDate = receivalDate;
            return this;
        }

        public Builder totalAmount(BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        public Builder status(OrderStatus status) {
            this.status = status;
            return this;
        }

        public Order build() {
            // Validate required fields
            if (userId <= 0) {
                throw new IllegalStateException("User ID is required");
            }
            if (orderDate == null) {
                throw new IllegalStateException("Order date is required");
            }
            if (status == null) {
                throw new IllegalStateException("Status is required");
            }

            return new Order(this);
        }
    }
}
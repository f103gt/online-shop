package com.internetshop.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

public class Order {
    private int id;
    private int userId;
    private Date orderDate;
    private Date receivalDate;
    private BigDecimal totalAmount;
    private OrderStatus status;

    public Order(int id, int userId, Date orderDate,
                 BigDecimal totalAmount, OrderStatus status) {
        this.id = id;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public Date getOrderDate() { return orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public OrderStatus getStatus() { return status; }

    public void setId(int id) {
        this.id = id;
    }

    // Add this getter
    public Optional<Date> getReceivalDate() {
        return Optional.ofNullable(receivalDate);
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}


package com.internetshop.model;

public class BlackList {
    private int id;
    private int userId;
    private int orderId;

    public BlackList(int id, int userId, int orderId) {
        this.id = id;
        this.userId = userId;
        this.orderId = orderId;
    }

    // Getters and setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getOrderId() { return orderId; }

    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
}
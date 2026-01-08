package com.trading.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class OrderRequest {
    @NotNull(message = "orderType is required")
    private String orderType; // BUY or SELL

    @NotNull(message = "orderStyle is required")
    private String orderStyle; // MARKET or LIMIT

    @NotNull(message = "symbol is required")
    private String symbol;

    @NotNull(message = "quantity is required")
    @Positive(message = "quantity must be greater than 0")
    private Double quantity;

    private Double price; // Required for LIMIT orders

    public OrderRequest() {
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getOrderStyle() {
        return orderStyle;
    }

    public void setOrderStyle(String orderStyle) {
        this.orderStyle = orderStyle;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}

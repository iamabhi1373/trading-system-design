package com.trading.sdk.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderRequest {
    private String orderType; // BUY or SELL
    private String orderStyle; // MARKET or LIMIT
    private String symbol;
    private Double quantity;
    private Double price; // Required for LIMIT orders

    public OrderRequest() {
    }

    public OrderRequest(String orderType, String orderStyle, String symbol, Double quantity) {
        this.orderType = orderType;
        this.orderStyle = orderStyle;
        this.symbol = symbol;
        this.quantity = quantity;
    }

    public OrderRequest(String orderType, String orderStyle, String symbol, Double quantity, Double price) {
        this(orderType, orderStyle, symbol, quantity);
        this.price = price;
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

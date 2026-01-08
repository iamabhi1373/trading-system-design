package com.trading.service;

import com.trading.model.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TradingService {
    
    private final List<Instrument> instruments;
    private final Map<String, Order> orders;
    private final List<Trade> trades;
    private final Map<String, PortfolioHolding> portfolio;

    public TradingService() {
        // Initialize with sample instruments
        this.instruments = new ArrayList<>(Arrays.asList(
            new Instrument("AAPL", "NASDAQ", "STOCK", 175.50),
            new Instrument("GOOGL", "NASDAQ", "STOCK", 142.30),
            new Instrument("MSFT", "NASDAQ", "STOCK", 378.85),
            new Instrument("TSLA", "NASDAQ", "STOCK", 248.42),
            new Instrument("BTC-USD", "CRYPTO", "CRYPTO", 43250.00)
        ));
        
        this.orders = new ConcurrentHashMap<>();
        this.trades = new ArrayList<>();
        this.portfolio = new ConcurrentHashMap<>();
    }

    public List<Instrument> getAllInstruments() {
        return new ArrayList<>(instruments);
    }

    public Instrument getInstrumentBySymbol(String symbol) {
        return instruments.stream()
                .filter(inst -> inst.getSymbol().equals(symbol))
                .findFirst()
                .orElse(null);
    }

    public Order placeOrder(OrderRequest request) {
        // Validate symbol exists
        Instrument instrument = getInstrumentBySymbol(request.getSymbol());
        if (instrument == null) {
            throw new IllegalArgumentException("Instrument " + request.getSymbol() + " not found");
        }

        // Validate order type
        String orderType = request.getOrderType().toUpperCase();
        if (!orderType.equals("BUY") && !orderType.equals("SELL")) {
            throw new IllegalArgumentException("orderType must be BUY or SELL");
        }

        // Validate order style
        String orderStyle = request.getOrderStyle().toUpperCase();
        if (!orderStyle.equals("MARKET") && !orderStyle.equals("LIMIT")) {
            throw new IllegalArgumentException("orderStyle must be MARKET or LIMIT");
        }

        // Validate price for LIMIT orders
        if (orderStyle.equals("LIMIT") && request.getPrice() == null) {
            throw new IllegalArgumentException("price is required for LIMIT orders");
        }

        if (orderStyle.equals("LIMIT") && request.getPrice() != null && request.getPrice() <= 0) {
            throw new IllegalArgumentException("price must be greater than 0");
        }

        // Create order
        Order order = new Order();
        order.setSymbol(request.getSymbol());
        order.setOrderType(orderType);
        order.setOrderStyle(orderStyle);
        order.setQuantity(request.getQuantity());
        order.setPrice(request.getPrice());
        order.setStatus("PLACED");

        // Try to execute order
        Trade trade = executeOrder(order, instrument);
        if (trade != null) {
            order.setStatus("EXECUTED");
        }

        orders.put(order.getOrderId(), order);
        return order;
    }

    private Trade executeOrder(Order order, Instrument instrument) {
        String orderType = order.getOrderType();
        String orderStyle = order.getOrderStyle();
        Double quantity = order.getQuantity();
        Double limitPrice = order.getPrice();
        Double currentPrice = instrument.getLastTradedPrice();

        // Determine execution price
        Double executionPrice;
        if (orderStyle.equals("MARKET")) {
            executionPrice = currentPrice;
        } else { // LIMIT
            executionPrice = limitPrice;
            
            // Check if limit order can execute
            if (orderType.equals("BUY") && limitPrice < currentPrice) {
                return null; // Limit buy price too low
            }
            if (orderType.equals("SELL") && limitPrice > currentPrice) {
                return null; // Limit sell price too high
            }
        }

        // Check if we can execute the sell order (have enough holdings)
        if (orderType.equals("SELL")) {
            PortfolioHolding holding = portfolio.get(order.getSymbol());
            if (holding == null || holding.getQuantity() < quantity) {
                return null; // Insufficient holdings
            }
        }

        // Create trade
        Trade trade = new Trade(order.getOrderId(), order.getSymbol(), quantity, executionPrice, orderType);
        trades.add(trade);

        // Update portfolio
        updatePortfolio(trade, instrument);

        return trade;
    }

    private void updatePortfolio(Trade trade, Instrument instrument) {
        String symbol = trade.getSymbol();
        PortfolioHolding holding = portfolio.computeIfAbsent(symbol, k -> new PortfolioHolding(symbol));

        if (trade.getSide().equals("BUY")) {
            // Buy: update average price and quantity
            Double totalCost = (holding.getQuantity() * holding.getAveragePrice()) + 
                             (trade.getQuantity() * trade.getPrice());
            holding.setQuantity(holding.getQuantity() + trade.getQuantity());
            holding.setAveragePrice(totalCost / holding.getQuantity());
        } else {
            // Sell: reduce quantity
            holding.setQuantity(holding.getQuantity() - trade.getQuantity());
            if (holding.getQuantity() <= 0) {
                holding.setQuantity(0.0);
                holding.setAveragePrice(0.0);
            }
        }

        // Update current value
        holding.setCurrentValue(holding.getQuantity() * instrument.getLastTradedPrice());
    }

    public Order getOrderById(String orderId) {
        return orders.get(orderId);
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public Order cancelOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        if (order.getStatus().equals("EXECUTED") || order.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("Cannot cancel order with status " + order.getStatus());
        }

        order.setStatus("CANCELLED");
        return order;
    }

    public List<Trade> getAllTrades() {
        return new ArrayList<>(trades);
    }

    public List<PortfolioHolding> getPortfolio() {
        // Update current values for all holdings
        portfolio.values().forEach(holding -> {
            Instrument instrument = getInstrumentBySymbol(holding.getSymbol());
            if (instrument != null) {
                holding.setCurrentValue(holding.getQuantity() * instrument.getLastTradedPrice());
            }
        });

        // Return only holdings with quantity > 0
        return portfolio.values().stream()
                .filter(h -> h.getQuantity() > 0)
                .collect(Collectors.toList());
    }
}

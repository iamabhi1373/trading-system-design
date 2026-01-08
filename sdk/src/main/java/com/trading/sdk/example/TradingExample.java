package com.trading.sdk.example;

import com.trading.sdk.client.TradingApiClient;
import com.trading.sdk.exception.TradingApiException;
import com.trading.sdk.model.*;

import java.util.List;

/**
 * Example usage of the Trading SDK
 */
public class TradingExample {
    public static void main(String[] args) {
        // Initialize the SDK client
        TradingApiClient client = new TradingApiClient("http://localhost:8080");

        try {
            // 1. Fetch available instruments
            System.out.println("=== Fetching Instruments ===");
            List<Instrument> instruments = client.getInstruments();
            instruments.forEach(System.out::println);

            // 2. Place a market buy order
            System.out.println("\n=== Placing Market Buy Order ===");
            Order buyOrder = client.placeMarketOrder("BUY", "AAPL", 10.0);
            System.out.println("Order placed: " + buyOrder);
            System.out.println("Order Status: " + buyOrder.getStatus());

            // 3. Place a limit sell order
            System.out.println("\n=== Placing Limit Sell Order ===");
            Order sellOrder = client.placeLimitOrder("SELL", "AAPL", 5.0, 180.0);
            System.out.println("Order placed: " + sellOrder);

            // 4. Check order status
            System.out.println("\n=== Checking Order Status ===");
            Order status = client.getOrderStatus(buyOrder.getOrderId());
            System.out.println("Order status: " + status);

            // 5. Fetch all trades
            System.out.println("\n=== Fetching Trades ===");
            List<Trade> trades = client.getTrades();
            trades.forEach(System.out::println);

            // 6. Fetch portfolio holdings
            System.out.println("\n=== Fetching Portfolio ===");
            List<PortfolioHolding> portfolio = client.getPortfolio();
            portfolio.forEach(System.out::println);

            // 7. Fetch all orders
            System.out.println("\n=== Fetching All Orders ===");
            List<Order> allOrders = client.getAllOrders();
            allOrders.forEach(System.out::println);

        } catch (TradingApiException e) {
            System.err.println("API Error: " + e.getStatusCode() + " - " + e.getErrorMessage());
        }
    }
}

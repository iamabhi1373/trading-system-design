package com.trading.sdk.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.trading.sdk.exception.TradingApiException;
import com.trading.sdk.model.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Trading API Client - SDK wrapper for Trading System REST API
 */
public class TradingApiClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    /**
     * Create a new TradingApiClient with default base URL (http://localhost:8080)
     */
    public TradingApiClient() {
        this("http://localhost:8080");
    }

    /**
     * Create a new TradingApiClient with custom base URL
     * @param baseUrl Base URL of the API (e.g., "http://localhost:8080")
     */
    public TradingApiClient(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Fetch list of tradable instruments
     * @return List of instruments
     * @throws TradingApiException if API call fails
     */
    public List<Instrument> getInstruments() throws TradingApiException {
        return executeGet("/api/v1/instruments", new TypeReference<List<Instrument>>() {});
    }

    /**
     * Place a new order
     * @param orderType BUY or SELL
     * @param orderStyle MARKET or LIMIT
     * @param symbol Instrument symbol
     * @param quantity Order quantity (must be > 0)
     * @param price Order price (required for LIMIT orders)
     * @return Created order
     * @throws TradingApiException if API call fails or validation fails
     */
    public Order placeOrder(String orderType, String orderStyle, String symbol, Double quantity, Double price) 
            throws TradingApiException {
        OrderRequest request = new OrderRequest(orderType, orderStyle, symbol, quantity, price);
        return executePost("/api/v1/orders", request, Order.class);
    }

    /**
     * Place a market order
     * @param orderType BUY or SELL
     * @param symbol Instrument symbol
     * @param quantity Order quantity (must be > 0)
     * @return Created order
     * @throws TradingApiException if API call fails or validation fails
     */
    public Order placeMarketOrder(String orderType, String symbol, Double quantity) throws TradingApiException {
        return placeOrder(orderType, "MARKET", symbol, quantity, null);
    }

    /**
     * Place a limit order
     * @param orderType BUY or SELL
     * @param symbol Instrument symbol
     * @param quantity Order quantity (must be > 0)
     * @param price Limit price (must be > 0)
     * @return Created order
     * @throws TradingApiException if API call fails or validation fails
     */
    public Order placeLimitOrder(String orderType, String symbol, Double quantity, Double price) 
            throws TradingApiException {
        return placeOrder(orderType, "LIMIT", symbol, quantity, price);
    }

    /**
     * Fetch order status by order ID
     * @param orderId Order ID
     * @return Order object
     * @throws TradingApiException if API call fails or order not found
     */
    public Order getOrderStatus(String orderId) throws TradingApiException {
        return executeGet("/api/v1/orders/" + orderId, new TypeReference<Order>() {});
    }

    /**
     * Fetch all orders
     * @return List of all orders
     * @throws TradingApiException if API call fails
     */
    public List<Order> getAllOrders() throws TradingApiException {
        return executeGet("/api/v1/orders", new TypeReference<List<Order>>() {});
    }

    /**
     * Cancel an order
     * @param orderId Order ID to cancel
     * @return Cancelled order
     * @throws TradingApiException if API call fails or order cannot be cancelled
     */
    public Order cancelOrder(String orderId) throws TradingApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/api/v1/orders/" + orderId))
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return objectMapper.readValue(response.body(), Order.class);
            } else {
                throw parseErrorResponse(response);
            }
        } catch (IOException | InterruptedException e) {
            throw new TradingApiException(500, "Error executing request: " + e.getMessage());
        }
    }

    /**
     * Fetch list of executed trades
     * @return List of trades
     * @throws TradingApiException if API call fails
     */
    public List<Trade> getTrades() throws TradingApiException {
        return executeGet("/api/v1/trades", new TypeReference<List<Trade>>() {});
    }

    /**
     * Fetch current portfolio holdings
     * @return List of portfolio holdings
     * @throws TradingApiException if API call fails
     */
    public List<PortfolioHolding> getPortfolio() throws TradingApiException {
        return executeGet("/api/v1/portfolio", new TypeReference<List<PortfolioHolding>>() {});
    }

    // Private helper methods

    private <T> T executeGet(String path, TypeReference<T> typeRef) throws TradingApiException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .GET()
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == HttpURLConnection.HTTP_OK) {
                return objectMapper.readValue(response.body(), typeRef);
            } else {
                throw parseErrorResponse(response);
            }
        } catch (IOException | InterruptedException e) {
            throw new TradingApiException(500, "Error executing GET request: " + e.getMessage());
        }
    }

    private <T> T executePost(String path, Object requestBody, Class<T> responseClass) throws TradingApiException {
        try {
            String requestBodyJson = objectMapper.writeValueAsString(requestBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + path))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == HttpURLConnection.HTTP_CREATED || 
                response.statusCode() == HttpURLConnection.HTTP_OK) {
                return objectMapper.readValue(response.body(), responseClass);
            } else {
                throw parseErrorResponse(response);
            }
        } catch (IOException | InterruptedException e) {
            throw new TradingApiException(500, "Error executing POST request: " + e.getMessage());
        }
    }

    private TradingApiException parseErrorResponse(HttpResponse<String> response) throws IOException {
        try {
            ErrorResponse errorResponse = objectMapper.readValue(response.body(), ErrorResponse.class);
            return new TradingApiException(response.statusCode(), errorResponse.getError());
        } catch (Exception e) {
            return new TradingApiException(response.statusCode(), 
                    "API Error: " + response.statusCode() + " - " + response.body());
        }
    }

    private static class ErrorResponse {
        private String error;

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }
}

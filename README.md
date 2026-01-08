# Trading System - REST API Backend & SDK

A simplified trading backend system with REST API endpoints and a Java SDK wrapper for easy integration.

## Project Structure

```
BAZ/
├── backend/              # Spring Boot REST API
│   ├── src/main/java/com/trading/
│   │   ├── controller/   # REST controllers
│   │   ├── service/      # Business logic
│   │   └── model/        # Data models
│   └── pom.xml
└── sdk/                  # Java SDK wrapper
    ├── src/main/java/com/trading/sdk/
    │   ├── client/       # API client
    │   ├── model/        # SDK models
    │   └── example/      # Usage examples
    └── pom.xml
```

User App
   ↓
SDK (client/)
   ↓ HTTP
Backend Controller
   ↓
Backend Service
   ↓
Database / Exchange



## Features

### REST API Endpoints

1. **Instrument APIs**
   - `GET /api/v1/instruments` - Fetch list of tradable instruments

2. **Order Management APIs**
   - `POST /api/v1/orders` - Place a new order (BUY/SELL, MARKET/LIMIT)
   - `GET /api/v1/orders/{orderId}` - Fetch order status
   - `GET /api/v1/orders` - Fetch all orders
   - `DELETE /api/v1/orders/{orderId}` - Cancel an order

3. **Trade APIs**
   - `GET /api/v1/trades` - Fetch list of executed trades

4. **Portfolio APIs**
   - `GET /api/v1/portfolio` - Fetch current portfolio holdings

### SDK Features

- Simple, intuitive API for all operations
- Type-safe models
- Exception handling with meaningful error messages
- Support for both market and limit orders

## Technology Stack

- **Backend**: Java 17, Spring Boot 3.2.0
- **API Format**: JSON
- **Storage**: In-memory (ConcurrentHashMap)
- **SDK**: Java 17, Jackson for JSON processing

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+

### Running the Backend API

1. Navigate to the backend directory:
```bash
cd backend
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Using the SDK

1. Build the SDK:
```bash
cd sdk
mvn clean install
```

2. Add the SDK as a dependency to your project, or use the example:
```bash
mvn exec:java -Dexec.mainClass="com.trading.sdk.example.TradingExample"
```

## API Usage Examples

### Place a Market Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderType": "BUY",
    "orderStyle": "MARKET",
    "symbol": "AAPL",
    "quantity": 10
  }'
```

### Place a Limit Order
```bash
curl -X POST http://localhost:8080/api/v1/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderType": "SELL",
    "orderStyle": "LIMIT",
    "symbol": "AAPL",
    "quantity": 5,
    "price": 180.0
  }'
```

### Get Order Status
```bash
curl http://localhost:8080/api/v1/orders/{orderId}
```

### Get Portfolio
```bash
curl http://localhost:8080/api/v1/portfolio
```

## SDK Usage Example

```java
import com.trading.sdk.client.TradingApiClient;
import com.trading.sdk.exception.TradingApiException;

// Initialize client
TradingApiClient client = new TradingApiClient("http://localhost:8080");

// Fetch instruments
List<Instrument> instruments = client.getInstruments();

// Place a market buy order
Order order = client.placeMarketOrder("BUY", "AAPL", 10.0);

// Place a limit sell order
Order limitOrder = client.placeLimitOrder("SELL", "AAPL", 5.0, 180.0);

// Check order status
Order status = client.getOrderStatus(order.getOrderId());

// Get trades
List<Trade> trades = client.getTrades();

// Get portfolio
List<PortfolioHolding> portfolio = client.getPortfolio();
```

## Order States

- `NEW` - Order created but not yet processed
- `PLACED` - Order placed in the system
- `EXECUTED` - Order executed successfully
- `CANCELLED` - Order cancelled by user

## Validation Rules

- Quantity must be greater than 0
- Price is required for LIMIT orders
- Price must be greater than 0 for LIMIT orders
- Order type must be BUY or SELL
- Order style must be MARKET or LIMIT
- Symbol must exist in the instruments list

## Sample Instruments

The system comes pre-loaded with the following instruments:
- AAPL (NASDAQ, STOCK)
- GOOGL (NASDAQ, STOCK)
- MSFT (NASDAQ, STOCK)
- TSLA (NASDAQ, STOCK)
- BTC-USD (CRYPTO, CRYPTO)

## Notes

- This is a simplified trading system with in-memory storage
- All data is lost when the application restarts
- Order execution is simplified and executes immediately if conditions are met
- For LIMIT orders, execution happens if the limit price is favorable compared to the current market price

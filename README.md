# Jackpot Backend Service

A backend service to manage jackpot contributions and rewards, developed with Spring Boot and Kafka.

## Features
- Publish a bet to Kafka.
- Listen to `jackpot-bets` topic and process bets for jackpot contributions.
- Check if a bet wins the jackpot reward.
- Support for fixed and variable configurations for both contributions and rewards.
- In-memory database for development.

## Requirements
- Java 21
- Maven
- Docker (for Kafka setup, optional)

## Quick Start

1. **Clone the repository:**
   ```bash
   git clone https://github.com/esecandel/jackpot.git
   cd jackpot
   ```
2. **Start Kafka (mandatory):**
   If you want to use a real Kafka instance, you can start one using Docker:
   ```bash
   docker-compose up -d
   ```
   Make sure to have a `docker-compose.yml` file configured for Kafka.

3. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

4. **Access the APIs:**
   - Use this URI to see the API doc: http://localhost:8080/swagger-ui.html

## API Usage

There are 2 previous created jackpots:
1. Fixed Contribution and Fixed Reward Jackpot
   - ID: `73cada80-12e4-46b7-a0cb-a5eb99d4cafa`
   - Contribution Type: FIXED (10%)
   - Reward Type: FIXED (1% chance)
   - Pool Limit: 10000.00 
2. Variable Contribution and Variable Reward Jackpot
   - ID: `83cada80-12e4-46b7-a0cb-a5eb99d4cafa`
   - Contribution Type: VARIABLE (starts at 20%, decreases to 5% as pool reaches limit)
   - Reward Type: VARIABLE (starts at 5% chance, increases to 100% as pool reaches limit)
   - Pool Limit: 20000.00

### Publish a Bet
```
POST /api/bets
Content-Type: application/json

{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "jackpotId": "73cada80-12e4-46b7-a0cb-a5eb99d4cafa",
  "amount": 100.00
}
```

Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174002",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "jackpotId": "73cada80-12e4-46b7-a0cb-a5eb99d4cafa",
  "amount": 100.00,
  "createdAt": "2023-11-15T12:00:00"
}
```

### Check Reward
```
GET /api/bets/123e4567-e89b-12d3-a456-426614174002/reward
```

Response (if reward exists):
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174003",
  "betId": "123e4567-e89b-12d3-a456-426614174002",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "jackpotId": "73cada80-12e4-46b7-a0cb-a5eb99d4cafa",
  "amount": 5000.00,
  "grantedAt": "2023-11-15T12:01:00"
}
```

Response (if no reward):
```
No reward for this bet
```

## Architecture

The application follows a layered architecture:

1. **API Layer**: REST controllers for handling HTTP requests
2. **Domain Layer**: Core business logic and domain models
3. **Infrastructure Layer**: Implementation details like repositories and messaging

### Jackpot Contribution Types
- **FIXED**: A fixed percentage of the bet amount is contributed to the jackpot
- **VARIABLE**: The contribution percentage decreases as the jackpot pool increases

### Jackpot Reward Types
- **FIXED**: A fixed chance for winning the jackpot
- **VARIABLE**: The chance increases as the jackpot pool increases, reaching 100% when the pool limit is hit

## Testing

To run unit and integration tests:
```bash
./mvnw test
```

## Kafka Integration

The application uses Kafka for asynchronous processing of bets:
- Bets are published to the `jackpot-bets` topic
- A consumer listens to this topic and processes bets for jackpot contributions

If you want to use a real Kafka instance instead of mocks, make sure to configure it in `application.properties`.

## License
MIT

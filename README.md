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
   git clone https://github.com/yourusername/jackpot.git
   cd jackpot
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or on Windows:
   ```bash
   mvnw.cmd spring-boot:run
   ```

3. **Access the APIs:**
   - Publish bet: `POST /api/bets`
   - Check reward: `GET /api/bets/{betId}/reward`

## API Usage

### Publish a Bet
```
POST /api/bets
Content-Type: application/json

{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "jackpotId": "123e4567-e89b-12d3-a456-426614174001",
  "amount": 100.00
}
```

Response:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174002",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "jackpotId": "123e4567-e89b-12d3-a456-426614174001",
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
  "jackpotId": "123e4567-e89b-12d3-a456-426614174001",
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

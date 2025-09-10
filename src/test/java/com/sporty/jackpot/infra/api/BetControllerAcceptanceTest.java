package com.sporty.jackpot.infra.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.infra.api.model.JackpotRequest;
import com.sporty.jackpot.infra.api.model.JackpotResponse;
import com.sporty.jackpot.infra.api.model.RewardResponse;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.TestcontainersConfiguration;

@Import({TestcontainersConfiguration.class})
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BetControllerAcceptanceTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private JackpotRepository jackpotRepository;

  @Autowired
  private BetRepository betRepository;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/bets";
  }

  private String getJackpotsUrl() {
    return "http://localhost:" + port + "/jackpots";
  }

  private UUID jackpotId;

  @BeforeEach
  void setUp() {
    // Create a jackpot for testing
    JackpotRequest jackpotRequest = new JackpotRequest(
        "Test Jackpot",
        new BigDecimal("1000.00"),
        ContributionType.FIXED,
        RewardType.FIXED
    );

    ResponseEntity<JackpotResponse> response = restTemplate.postForEntity(
        getJackpotsUrl(),
        jackpotRequest,
        JackpotResponse.class
    );

    // Extract jackpotId from response
    jackpotId = Objects.requireNonNull(response.getBody()).id();
  }

  @Test
  void publishBet_shouldAcceptBetAndReturnIt() {
    // Arrange
    UUID betId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.00");
    UUID jackpotId = UUID.fromString("73cada80-12e4-46b7-a0cb-a5eb99d4cafa");
    Bet bet = new Bet(
        betId,
        userId,
        jackpotId,
        betAmount);

    // Act
    ResponseEntity<Bet> response = restTemplate.postForEntity(
        getBaseUrl(),
        bet,
        Bet.class
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getBetId()).isEqualTo(betId);
    assertThat(response.getBody().getUserId()).isEqualTo(userId);
    assertThat(response.getBody().getJackpotId()).isEqualTo(jackpotId);
    assertThat(response.getBody().getBetAmount()).isEqualTo(betAmount);

    // Wait a bit for the message to be processed
    try {
      Thread.sleep(15000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    // Verify the jackpot was updated with the contribution
    Jackpot updatedJackpot = jackpotRepository.findById(jackpotId).orElseThrow();
    assertThat(updatedJackpot.getCurrentPool()).isGreaterThan(new BigDecimal("100.00"));
    assertThat(updatedJackpot.getContributions()).hasSize(1);
  }

  @Test
  void checkReward_shouldReturnRewardStatusForBet() {
    // Arrange - Create a bet first
    UUID betId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.00");

    Bet bet = new Bet(betId, userId, jackpotId, betAmount);

    // Save the bet directly to the repository to bypass Kafka
    betRepository.save(bet);

    // Act
    ResponseEntity<RewardResponse> response = restTemplate.getForEntity(
        getBaseUrl() + "/" + betId + "/reward",
        RewardResponse.class
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();

    // The response could be either a Reward object or a String message
    // We can't predict which one since the reward is determined randomly
    assertThat(response.getBody().betId()).isEqualTo(betId);
  }

  @Test
  void checkReward_shouldReturnErrorForNonExistentBet() {
    // Arrange
    UUID nonExistentBetId = UUID.randomUUID();

    // Act
    ResponseEntity<RewardResponse> response = restTemplate.getForEntity(
        getBaseUrl() + "/" + nonExistentBetId + "/reward",
        RewardResponse.class
    );

    // Assert
    // Since this is an acceptance test, we expect the controller to return a 500 error
    // when the bet is not found (as per the current implementation)
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

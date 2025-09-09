package com.sporty.jackpot.infra.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.infra.api.model.JackpotRequest;
import com.sporty.jackpot.infra.api.model.JackpotResponse;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.utility.TestcontainersConfiguration;

@Import({TestcontainersConfiguration.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JackpotControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/jackpots";
  }

  @Test
  void create_shouldCreateJackpotAndReturnCreatedStatus() {
    // Arrange
    JackpotRequest request = new JackpotRequest(
        "Test Jackpot",
        new BigDecimal("1000.00"),
        ContributionType.VARIABLE,
        RewardType.FIXED
    );

    // Act
    ResponseEntity<JackpotResponse> response = restTemplate.postForEntity(
        getBaseUrl(),
        request,
        JackpotResponse.class
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().id()).isNotNull();
    assertThat(response.getBody().name()).isEqualTo("Test Jackpot");
    assertThat(response.getBody().initialPool()).isEqualTo(new BigDecimal("1000.00"));
    assertThat(response.getBody().contributionType()).isEqualTo(ContributionType.VARIABLE);
    assertThat(response.getBody().rewardType()).isEqualTo(RewardType.FIXED);
  }

  @Test
  void getAll_shouldReturnAllJackpots() {
    // Arrange
    JackpotRequest request1 = new JackpotRequest(
        "Jackpot 1",
        new BigDecimal("1000.00"),
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    JackpotRequest request2 = new JackpotRequest(
        "Jackpot 2",
        new BigDecimal("2000.00"),
        ContributionType.FIXED,
        RewardType.VARIABLE
    );

    restTemplate.postForEntity(getBaseUrl(), request1, JackpotResponse.class);
    restTemplate.postForEntity(getBaseUrl(), request2, JackpotResponse.class);

    // Act
    ResponseEntity<JackpotResponse[]> response = restTemplate.getForEntity(
        getBaseUrl(),
        JackpotResponse[].class
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isEqualTo(4);

    List<String> jackpotNames = Arrays.stream(response.getBody())
        .map(JackpotResponse::name)
        .toList();
    assertThat(jackpotNames).containsExactlyInAnyOrder(
        "Super Fixed Jackpot",
        "Super Variable Jackpot",
        "Jackpot 1",
        "Jackpot 2");
  }

  @Test
  void getById_shouldReturnJackpotWhenExists() {
    // Arrange
    JackpotRequest request = new JackpotRequest(
        "Test Jackpot",
        new BigDecimal("1000.00"),
        ContributionType.VARIABLE,
        RewardType.FIXED
    );

    ResponseEntity<JackpotResponse> createResponse = restTemplate.postForEntity(
        getBaseUrl(),
        request,
        JackpotResponse.class
    );

    UUID jackpotId = Objects.requireNonNull(createResponse.getBody()).id();

    // Act
    ResponseEntity<JackpotResponse> response = restTemplate.getForEntity(
        getBaseUrl() + "/" + jackpotId,
        JackpotResponse.class
    );

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().id()).isEqualTo(jackpotId);
    assertThat(response.getBody().name()).isEqualTo("Test Jackpot");
  }

  @Test
  void delete_shouldRemoveJackpot() {
    // Arrange
    JackpotRequest request = new JackpotRequest(
        "Test Jackpot",
        new BigDecimal("1000.00"),
        ContributionType.VARIABLE,
        RewardType.FIXED
    );

    ResponseEntity<JackpotResponse> createResponse = restTemplate.postForEntity(
        getBaseUrl(),
        request,
        JackpotResponse.class
    );

    UUID jackpotId = Objects.requireNonNull(createResponse.getBody()).id();

    // Act
    ResponseEntity<Void> deleteResponse = restTemplate.exchange(
        getBaseUrl() + "/" + jackpotId,
        HttpMethod.DELETE,
        null,
        Void.class
    );

    // Assert
    assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

    // Verify the jackpot is deleted
    ResponseEntity<String> getResponse = restTemplate.getForEntity(
        getBaseUrl() + "/" + jackpotId,
        String.class
    );

    assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}

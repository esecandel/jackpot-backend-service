package com.sporty.jackpot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.infra.api.model.JackpotRequest;
import com.sporty.jackpot.infra.api.model.JackpotResponse;
import com.sporty.jackpot.infra.persistence.JackpotInMemoryRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JackpotCrudServiceTest {

    private JackpotCrudService jackpotCrudService;

    @BeforeEach
    void setUp() {
        jackpotCrudService = new JackpotCrudService(new JackpotInMemoryRepository());
    }

    @Test
    void create_shouldCreateJackpotAndReturnResponse() {
        // Arrange
        String name = "Test Jackpot";
        BigDecimal initialPool = new BigDecimal("1000.00");
        JackpotRequest request = new JackpotRequest(
            name,
            initialPool,
            ContributionType.VARIABLE,
            RewardType.FIXED
        );

        // Act
        JackpotResponse response = jackpotCrudService.create(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isNotNull();
        assertThat(response.name()).isEqualTo(name);
        assertThat(response.initialPool()).isEqualTo(initialPool);
        assertThat(response.contributionType()).isEqualTo(ContributionType.VARIABLE);
        assertThat(response.rewardType()).isEqualTo(RewardType.FIXED);
        assertThat(response.createdAt()).isNotNull();
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
        
        jackpotCrudService.create(request1);
        jackpotCrudService.create(request2);

        // Act
        List<JackpotResponse> responses = jackpotCrudService.getAll();

        // Assert
        assertThat(responses).hasSize(4); // Including the initial jackpots in the in-memory repo
        assertThat(responses).extracting("name").containsExactlyInAnyOrder(
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
        JackpotResponse createdResponse = jackpotCrudService.create(request);
        UUID jackpotId = createdResponse.id();

        // Act
        JackpotResponse response = jackpotCrudService.getById(jackpotId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(jackpotId);
        assertThat(response.name()).isEqualTo("Test Jackpot");
    }

    @Test
    void getById_shouldThrowExceptionWhenJackpotDoesNotExist() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> jackpotCrudService.getById(nonExistentId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Jackpot not found");
    }

    @Test
    void delete_shouldRemoveJackpotFromRepository() {
        // Arrange
        JackpotRequest request = new JackpotRequest(
            "Test Jackpot",
            new BigDecimal("1000.00"),
            ContributionType.VARIABLE,
            RewardType.FIXED
        );
        JackpotResponse createdResponse = jackpotCrudService.create(request);
        UUID jackpotId = createdResponse.id();

        // Act
        jackpotCrudService.delete(jackpotId);

        // Assert
        assertThatThrownBy(() -> jackpotCrudService.getById(jackpotId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Jackpot not found");
    }
}
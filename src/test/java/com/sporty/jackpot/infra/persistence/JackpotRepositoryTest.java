package com.sporty.jackpot.infra.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JackpotRepositoryTest {

  private final JackpotRepository jackpotRepository = new JackpotInMemoryRepository();

  @Test
  void saveJackpot_shouldPersistJackpotInDatabase() {
    // Arrange
    String name = "Test Jackpot";
    BigDecimal initialPool = new BigDecimal("1000.00");
    ContributionType contributionType = ContributionType.VARIABLE;
    RewardType rewardType = RewardType.FIXED;
    Jackpot jackpot = new Jackpot(name, initialPool, contributionType, rewardType);

    // Act
    jackpotRepository.save(jackpot);
    Optional<Jackpot> savedOpt = jackpotRepository.findById(jackpot.getJackpotId());
    assertThat(savedOpt).isPresent();
    Jackpot savedJackpot = savedOpt.get();

    // Assert
    assertThat(savedJackpot.getJackpotId()).isNotNull();
    assertThat(savedJackpot.getName()).isEqualTo(name);
    assertThat(savedJackpot.getInitialPool()).isEqualTo(initialPool);
    assertThat(savedJackpot.getContributionType()).isEqualTo(contributionType);
    assertThat(savedJackpot.getRewardType()).isEqualTo(rewardType);
    assertThat(savedJackpot.getCreatedAt()).isNotNull();
  }

  @Test
  void findById_shouldReturnJackpotIfExists() {
    // Arrange
    String name = "Test Jackpot";
    BigDecimal initialPool = new BigDecimal("500.00");
    ContributionType contributionType = ContributionType.FIXED;
    RewardType rewardType = RewardType.VARIABLE;
    Jackpot jackpot = new Jackpot(name, initialPool, contributionType, rewardType);
    jackpotRepository.save(jackpot);

    // Act
    Optional<Jackpot> foundJackpot = jackpotRepository.findById(jackpot.getJackpotId());

    // Assert
    assertThat(foundJackpot).isPresent();
    assertThat(foundJackpot.get().getJackpotId()).isEqualTo(jackpot.getJackpotId());
  }

  @Test
  void findAll_shouldReturnAllJackpots() {
    // Arrange
    Jackpot jackpot1 = new Jackpot("Jackpot 1", new BigDecimal("100.00"), ContributionType.VARIABLE,
        RewardType.FIXED);
    Jackpot jackpot2 = new Jackpot("Jackpot 2", new BigDecimal("200.00"), ContributionType.FIXED,
        RewardType.VARIABLE);

    jackpotRepository.save(jackpot1);
    jackpotRepository.save(jackpot2);

    // Act
    List<Jackpot> jackpots = jackpotRepository.findAll();

    // Assert
    assertThat(jackpots).hasSize(2);
    assertThat(jackpots).extracting("name").containsExactlyInAnyOrder("Jackpot 1", "Jackpot 2");
  }

  @Test
  void deleteById_shouldRemoveJackpotFromDatabase() {
    // Arrange
    Jackpot jackpot = new Jackpot("Jackpot to delete", new BigDecimal("300.00"),
        ContributionType.VARIABLE, RewardType.FIXED);
    jackpotRepository.save(jackpot);

    // Act
    jackpotRepository.delete(jackpot.getJackpotId());
    Optional<Jackpot> foundJackpot = jackpotRepository.findById(jackpot.getJackpotId());

    // Assert
    assertThat(foundJackpot).isNotPresent();
  }
}

package com.sporty.jackpot.infra.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.jackpot.domain.model.Reward;
import com.sporty.jackpot.domain.persistence.RewardRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RewardRepositoryTest {

  private final RewardRepository rewardRepository = new RewardInMemoryRepository();

  @Test
  void saveReward_shouldPersistRewardInDatabase() {
    // Arrange
    UUID jackpotId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID betId = UUID.randomUUID();
    BigDecimal amount = new BigDecimal("50.00");
    Reward reward = new Reward(betId, jackpotId, userId, amount);

    // Act
    rewardRepository.save(reward);

    // Assert
    Optional<Reward> savedOpt = rewardRepository.findById(reward.getRewardId());
    assertThat(savedOpt).isPresent();
    Reward savedReward = savedOpt.get();

    // Assert
    assertThat(savedReward.getRewardId()).isEqualTo(reward.getRewardId());
    assertThat(savedReward.getBetId()).isNotNull();
    assertThat(savedReward.getUserId()).isEqualTo(userId);
    assertThat(savedReward.getJackpotId()).isEqualTo(jackpotId);
    assertThat(savedReward.getAmount()).isEqualTo(amount);
    assertThat(savedReward.getGrantedAt()).isNotNull();
  }
}
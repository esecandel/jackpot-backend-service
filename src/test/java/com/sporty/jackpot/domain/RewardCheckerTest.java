package com.sporty.jackpot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import java.math.BigDecimal;
import java.util.Random;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RewardCheckerTest {

  private final Random mockRandom = mock(Random.class);
  private final RewardChecker rewardChecker = new RewardChecker(mockRandom);

  @Test
  void evaluate_shouldUseFixedChance_whenRewardTypeIsFixed() throws Exception {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null,
        RewardType.FIXED
    );

    // Test winning case (random value < fixed chance of 0.01)
    when(mockRandom.nextDouble()).thenReturn(0.005);
    boolean result = rewardChecker.evaluate(jackpot);
    assertThat(result).isTrue();

    // Test losing case (random value > fixed chance of 0.01)
    when(mockRandom.nextDouble()).thenReturn(0.02);
    result = rewardChecker.evaluate(jackpot);
    assertThat(result).isFalse();

  }

  @Test
  void evaluate_shouldUseVariableChance_whenRewardTypeIsVariable() throws Exception {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("2000.00"); // Pool ratio = 2.0

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null,
        RewardType.VARIABLE
    );
    jackpot.setCurrentPool(currentPool);

    // For pool ratio 2.0, adjusted chance should be 0.005 + (2.0 * 0.001) = 0.007
    // Test winning case (random value < adjusted chance of 0.007)
    when(mockRandom.nextDouble()).thenReturn(0.006);
    boolean result = rewardChecker.evaluate(jackpot);
    assertThat(result).isTrue();

    // Test losing case (random value > adjusted chance of 0.007)
    when(mockRandom.nextDouble()).thenReturn(0.008);
    result = rewardChecker.evaluate(jackpot);
    assertThat(result).isFalse();

  }

  @Test
  void evaluate_shouldReturnTrue_whenPoolLimitIsReached() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("10001.00"); // Just over 10x initial pool

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null,
        RewardType.VARIABLE
    );
    jackpot.setCurrentPool(currentPool);

    // Act
    boolean result = rewardChecker.evaluate(jackpot);

    // Assert
    // Should be true regardless of random value because pool limit is reached
    assertThat(result).isTrue();
  }

  @Test
  void evaluate_shouldHandleNullCurrentPool() throws Exception {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null,
        RewardType.VARIABLE
    );
    jackpot.setCurrentPool(null); // Explicitly set to null

    // Should use initial pool, so pool ratio = 1.0
    // Adjusted chance should be 0.005 + (1.0 * 0.001) = 0.006
    when(mockRandom.nextDouble()).thenReturn(0.005);
    boolean result = rewardChecker.evaluate(jackpot);
    assertThat(result).isTrue();

  }

  @Test
  void evaluate_shouldUseDefaultChance_whenRewardTypeIsNull() throws Exception {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null,
        null // Null reward type
    );

    // Should use default fixed chance (0.01)
    // Test winning case
    when(mockRandom.nextDouble()).thenReturn(0.005);
    boolean result = rewardChecker.evaluate(jackpot);
    assertThat(result).isTrue();

    // Test losing case
    when(mockRandom.nextDouble()).thenReturn(0.02);
    result = rewardChecker.evaluate(jackpot);
    assertThat(result).isFalse();

  }

  @Test
  void evaluate_shouldCapAdjustedChanceAtOne() throws Exception {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("1000000.00"); // Very large pool

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null,
        RewardType.VARIABLE
    );
    jackpot.setCurrentPool(currentPool);

    // With such a large pool ratio, the adjusted chance would be > 1.0
    // but should be capped at 1.0
    // Even with random value of 0.999, should still win
    when(mockRandom.nextDouble()).thenReturn(0.999);
    boolean result = rewardChecker.evaluate(jackpot);
    assertThat(result).isTrue();

  }
}
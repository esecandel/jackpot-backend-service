package com.sporty.jackpot.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;

class PoolGrowerTest {

  @Test
  void getAdjustedPercentage_shouldReturnCorrectPercentage_whenPoolRatioIsOne() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("1000.00"); // Same as initial pool
    BigDecimal initialVariablePercentage = new BigDecimal("0.20"); // 20%
    BigDecimal decreaseRate = new BigDecimal("0.05"); // 5% decrease rate

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setCurrentPool(currentPool);

    // Act
    BigDecimal result = PoolGrower.getAdjustedPercentage(jackpot, initialVariablePercentage,
        decreaseRate);

    // Assert
    // Expected: 20% - (1.0 * 5%) = 15%
    BigDecimal expected = new BigDecimal("0.15");
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void getAdjustedPercentage_shouldReturnLowerPercentage_whenPoolRatioIsGreaterThanOne() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("2000.00"); // 2x initial pool
    BigDecimal initialVariablePercentage = new BigDecimal("0.20"); // 20%
    BigDecimal decreaseRate = new BigDecimal("0.05"); // 5% decrease rate

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setCurrentPool(currentPool);

    // Act
    BigDecimal result = PoolGrower.getAdjustedPercentage(jackpot, initialVariablePercentage,
        decreaseRate);

    // Assert
    // Expected: 20% - (2.0 * 5%) = 10%
    BigDecimal expected = new BigDecimal("0.10");
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void getAdjustedPercentage_shouldReturnMinimumPercentage_whenCalculatedPercentageIsBelowMinimum() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("4000.00"); // 4x initial pool
    BigDecimal initialVariablePercentage = new BigDecimal("0.20"); // 20%
    BigDecimal decreaseRate = new BigDecimal("0.05"); // 5% decrease rate

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setCurrentPool(currentPool);

    // Act
    BigDecimal result = PoolGrower.getAdjustedPercentage(jackpot, initialVariablePercentage,
        decreaseRate);

    // Assert
    // Raw calculation: 20% - (4.0 * 5%) = 0%
    // But minimum is 5%, so expected is 5%
    BigDecimal expected = new BigDecimal("0.05");
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void getAdjustedPercentage_shouldUseInitialPool_whenCurrentPoolIsNull() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal initialVariablePercentage = new BigDecimal("0.20"); // 20%
    BigDecimal decreaseRate = new BigDecimal("0.05"); // 5% decrease rate

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setCurrentPool(null); // Current pool is null

    // Act
    BigDecimal result = PoolGrower.getAdjustedPercentage(jackpot, initialVariablePercentage,
        decreaseRate);

    // Assert
    // Should use initial pool, so pool ratio is 1.0
    // Expected: 20% - (1.0 * 5%) = 15%
    BigDecimal expected = new BigDecimal("0.15");
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void getAdjustedPercentage_shouldHandleDecimalPrecision_whenPoolRatioHasMoreThanTwoDecimals() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("1333.33"); // 1.33333x initial pool
    BigDecimal initialVariablePercentage = new BigDecimal("0.20"); // 20%
    BigDecimal decreaseRate = new BigDecimal("0.05"); // 5% decrease rate

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setCurrentPool(currentPool);

    // Act
    BigDecimal result = PoolGrower.getAdjustedPercentage(jackpot, initialVariablePercentage,
        decreaseRate);

    // Assert
    // Pool ratio should be rounded to 1.33
    // Expected: 20% - (1.33 * 5%) = 20% - 6.65% = 13.35%
    BigDecimal poolRatio = currentPool.divide(initialPool, 2, RoundingMode.HALF_UP);
    BigDecimal expectedDecrease = poolRatio.multiply(decreaseRate);
    BigDecimal expected = initialVariablePercentage.subtract(expectedDecrease);

    assertThat(result).isEqualByComparingTo(expected);
  }
}
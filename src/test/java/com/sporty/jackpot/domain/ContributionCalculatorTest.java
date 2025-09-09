package com.sporty.jackpot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContributionCalculatorTest {

  @Test
  void calculate_shouldReturnFixedPercentage_whenContributionTypeIsFixed() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal betAmount = new BigDecimal("100.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.FIXED,
        RewardType.FIXED
    );

    // Act
    BigDecimal result = ContributionCalculator.calculate(jackpot, betAmount);

    // Assert
    // Fixed percentage is 10%
    BigDecimal expected = new BigDecimal("10.00");
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void calculate_shouldUsePoolGrower_whenContributionTypeIsVariable() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal currentPool = new BigDecimal("2000.00");
    BigDecimal betAmount = new BigDecimal("100.00");
    BigDecimal adjustedPercentage = new BigDecimal("0.15");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setCurrentPool(currentPool);

    try (MockedStatic<PoolGrower> poolGrowerMock = Mockito.mockStatic(PoolGrower.class)) {
      // Mock the static method call to PoolGrower
      poolGrowerMock.when(() -> PoolGrower.getAdjustedPercentage(
              eq(jackpot),
              any(BigDecimal.class),
              any(BigDecimal.class)))
          .thenReturn(adjustedPercentage);

      // Act
      BigDecimal result = ContributionCalculator.calculate(jackpot, betAmount);

      // Assert
      // Should use the mocked adjusted percentage (15%)
      BigDecimal expected = new BigDecimal("15.00");
      assertThat(result).isEqualByComparingTo(expected);

      // Verify the static method was called with correct parameters
      poolGrowerMock.verify(() -> PoolGrower.getAdjustedPercentage(
          eq(jackpot),
          eq(new BigDecimal("0.20")), // initialVariablePercentage
          eq(new BigDecimal("0.05"))  // decreaseRate
      ));
    }
  }

  @Test
  void calculate_shouldReturnFixedPercentage_whenContributionTypeIsNull() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal betAmount = new BigDecimal("100.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        null, // Null contribution type
        RewardType.FIXED
    );

    // Act
    BigDecimal result = ContributionCalculator.calculate(jackpot, betAmount);

    // Assert
    // Should default to fixed percentage (10%)
    BigDecimal expected = new BigDecimal("10.00");
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void calculate_shouldHandleZeroBetAmount() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal betAmount = BigDecimal.ZERO;

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.FIXED,
        RewardType.FIXED
    );

    // Act
    BigDecimal result = ContributionCalculator.calculate(jackpot, betAmount);

    // Assert
    // Any percentage of zero is zero
    BigDecimal expected = BigDecimal.ZERO;
    assertThat(result).isEqualByComparingTo(expected);
  }

  @Test
  void calculate_shouldHandleLargeBetAmount() {
    // Arrange
    BigDecimal initialPool = new BigDecimal("1000.00");
    BigDecimal betAmount = new BigDecimal("1000000.00"); // 1 million

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.FIXED,
        RewardType.FIXED
    );

    // Act
    BigDecimal result = ContributionCalculator.calculate(jackpot, betAmount);

    // Assert
    // 10% of 1 million is 100,000
    BigDecimal expected = new BigDecimal("100000.00");
    assertThat(result).isEqualByComparingTo(expected);
  }
}
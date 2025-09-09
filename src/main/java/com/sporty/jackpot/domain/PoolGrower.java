package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.Jackpot;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PoolGrower {

  public static BigDecimal getAdjustedPercentage(
      Jackpot jackpot,
      BigDecimal initialVariablePercentage,
      BigDecimal decreaseRate) {
    BigDecimal currentPool =
        jackpot.getCurrentPool() != null ? jackpot.getCurrentPool() : jackpot.getInitialPool();
    BigDecimal poolRatio = currentPool.divide(jackpot.getInitialPool(), 2, RoundingMode.HALF_UP);

    // Decrease percentage as pool grows, but never below minimum
    BigDecimal adjustedPercentage = initialVariablePercentage.subtract(
        poolRatio.multiply(decreaseRate));

    // Ensure minimum percentage
    BigDecimal minPercentage = new BigDecimal("0.05"); // 5%
    if (adjustedPercentage.compareTo(minPercentage) < 0) {
      adjustedPercentage = minPercentage;
    }
    return adjustedPercentage;
  }
}

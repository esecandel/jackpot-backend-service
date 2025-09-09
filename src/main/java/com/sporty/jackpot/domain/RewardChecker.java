package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

public class RewardChecker {

  private final Random random;

  public RewardChecker(Random random) {
    this.random = random;
  }

  public boolean evaluate(Jackpot jackpot) {
    // Default reward chances (can be made configurable)
    BigDecimal fixedChance = new BigDecimal("0.01"); // 1%
    BigDecimal initialVariableChance = new BigDecimal("0.005"); // 0.5%
    BigDecimal increaseRate = new BigDecimal("0.001"); // 0.1% increase rate per pool ratio
    BigDecimal poolLimitMultiplier = new BigDecimal("10"); // Pool limit is 10x initial pool

    if (jackpot.getRewardType() == RewardType.FIXED) {
      // Fixed chance for reward
      return random.nextDouble() < fixedChance.doubleValue();
    } else if (jackpot.getRewardType() == RewardType.VARIABLE) {
      // Variable chance that increases as pool grows
      BigDecimal currentPool =
          jackpot.getCurrentPool() != null ? jackpot.getCurrentPool() : jackpot.getInitialPool();
      BigDecimal poolRatio = currentPool.divide(jackpot.getInitialPool(), 2, RoundingMode.HALF_UP);

      // Check if pool has hit the limit
      BigDecimal poolLimit = jackpot.getInitialPool().multiply(poolLimitMultiplier);
      if (currentPool.compareTo(poolLimit) >= 0) {
        // 100% chance of reward if pool limit is reached
        return true;
      }

      // Increase chance as pool grows
      BigDecimal adjustedChance = initialVariableChance.add(
          poolRatio.multiply(increaseRate));

      // Ensure maximum chance is 100%
      BigDecimal maxChance = BigDecimal.ONE;
      if (adjustedChance.compareTo(maxChance) > 0) {
        adjustedChance = maxChance;
      }

      return random.nextDouble() < adjustedChance.doubleValue();
    }

    // Default fallback
    return random.nextDouble() < fixedChance.doubleValue();
  }
}

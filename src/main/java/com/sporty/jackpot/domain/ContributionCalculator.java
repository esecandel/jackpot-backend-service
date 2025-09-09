package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import java.math.BigDecimal;

public class ContributionCalculator {

  public static BigDecimal calculate(Jackpot jackpot, BigDecimal betAmount) {
    //TODO setup the variable percentage from API. Fixed for now.
    BigDecimal fixedPercentage = new BigDecimal("0.10"); // 10%
    BigDecimal initialVariablePercentage = new BigDecimal("0.20"); // 20%
    BigDecimal decreaseRate = new BigDecimal("0.05"); // 5% decrease rate

    if (jackpot.getContributionType() == ContributionType.FIXED) {
      return betAmount.multiply(fixedPercentage);
    } else if (jackpot.getContributionType() == ContributionType.VARIABLE) {
      // Variable contribution: starts higher and decreases as pool grows
      BigDecimal adjustedPercentage = PoolGrower.getAdjustedPercentage(jackpot,
          initialVariablePercentage,
          decreaseRate);

      return betAmount.multiply(adjustedPercentage);
    }

    return betAmount.multiply(fixedPercentage);
  }
}

package com.sporty.jackpot.infra.api.model;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.RewardType;
import java.math.BigDecimal;


public record JackpotRequest(
    String name,
    BigDecimal initialPool,
    ContributionType contributionType,
    RewardType rewardType
) {

}



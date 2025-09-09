package com.sporty.jackpot.infra.api.model;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.RewardType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record JackpotResponse(
    UUID id,
    String name,
    BigDecimal initialPool,
    BigDecimal currentPool,
    ContributionType contributionType,
    RewardType rewardType,
    LocalDateTime createdAt
) {

}

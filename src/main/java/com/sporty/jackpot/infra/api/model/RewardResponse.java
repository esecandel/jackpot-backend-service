package com.sporty.jackpot.infra.api.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record RewardResponse(
    UUID betId,
    UUID jackpotId,
    UUID userId,
    BigDecimal amount,
    LocalDateTime grantedAt,
    String message) {

}

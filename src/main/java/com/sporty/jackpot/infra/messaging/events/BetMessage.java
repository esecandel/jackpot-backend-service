package com.sporty.jackpot.infra.messaging.events;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record BetMessage(
        UUID betRequestId,
        UUID userId,
        UUID jackpotId,
        BigDecimal betAmount,
        LocalDateTime createdAt
) {

}
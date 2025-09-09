package com.sporty.jackpot.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Bet {

  private UUID betId;
  private UUID userId;
  private UUID jackpotId;
  private BigDecimal betAmount;
  private LocalDateTime createdAt = LocalDateTime.now();

  public Bet(UUID betId, UUID userId, UUID jackpotId, BigDecimal betAmount) {
    this.betId = betId;
    this.userId = userId;
    this.jackpotId = jackpotId;
    this.betAmount = betAmount;
  }
}


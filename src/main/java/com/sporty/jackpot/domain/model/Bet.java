package com.sporty.jackpot.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class Bet {

  private UUID betId = UUID.randomUUID();
  private UUID userId;
  private UUID jackpotId;
  private BigDecimal betAmount;
  private LocalDateTime createdAt = LocalDateTime.now();

  public Bet(UUID userId, UUID jackpotId, BigDecimal betAmount) {
    this.userId = userId;
    this.jackpotId = jackpotId;
    this.betAmount = betAmount;
  }
}


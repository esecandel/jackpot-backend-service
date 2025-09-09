package com.sporty.jackpot.domain.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Reward {

  public Reward(UUID betId, UUID jackpotId, UUID userId, BigDecimal amount) {
    this.betId = betId;
    this.jackpotId = jackpotId;
    this.userId = userId;
    this.amount = amount;
  }

  private UUID rewardId = UUID.randomUUID();
  private UUID betId;
  private UUID jackpotId;
  private UUID userId;
  private RewardType rewardType;
  private BigDecimal amount;
  private LocalDateTime grantedAt = LocalDateTime.now();

}



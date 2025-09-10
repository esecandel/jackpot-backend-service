package com.sporty.jackpot.domain.model;


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
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Contribution {

  private UUID betId;
  private BigDecimal contributionAmount;
  private LocalDateTime createdAt;

  public Contribution(UUID betId, BigDecimal contributionAmount) {
    this.betId = betId;
    this.contributionAmount = contributionAmount;
    this.createdAt = LocalDateTime.now();
  }
}



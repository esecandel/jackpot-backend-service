package com.sporty.jackpot.domain.model;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@AllArgsConstructor
@ToString
public class Jackpot {

  private UUID jackpotId = UUID.randomUUID();
  private String name;
  private BigDecimal initialPool = BigDecimal.ZERO;
  private BigDecimal currentPool = BigDecimal.ZERO;
  private LocalDateTime createdAt = LocalDateTime.now();
  private ContributionType contributionType;
  private RewardType rewardType;
  private List<Contribution> contributions = new ArrayList<>();

  public Jackpot(String name,
      BigDecimal initialPool,
      ContributionType contributionType,
      RewardType rewardType) {
    this.name = name;
    this.initialPool = initialPool;
    this.currentPool = initialPool;
    this.contributionType = contributionType;
    this.rewardType = rewardType;
  }
}


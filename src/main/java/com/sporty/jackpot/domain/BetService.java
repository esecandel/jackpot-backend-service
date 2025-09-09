package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.Reward;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.domain.persistence.RewardRepository;
import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class BetService {

  private final JackpotRepository jackpotRepository;
  private final BetRepository betRepository;
  private final RewardRepository rewardRepository;
  private final RewardChecker rewardChecker = new RewardChecker(new Random());

  public BetService(JackpotRepository jackpotRepository,
      BetRepository betRepository,
      RewardRepository rewardRepository) {
    this.jackpotRepository = jackpotRepository;
    this.betRepository = betRepository;
    this.rewardRepository = rewardRepository;
  }

  public Reward checkReward(UUID betId) {
    Bet bet = betRepository.findById(betId)
        .orElseThrow(() -> new RuntimeException("Bet not found"));
    Jackpot jackpot = jackpotRepository.findById(bet.getJackpotId())
        .orElseThrow(() -> new RuntimeException("Jackpot not found"));

    // Evaluate if the bet wins a reward
    boolean isWinningBet = rewardChecker.evaluate(jackpot);
    if (isWinningBet) {
      // Create reward
      BigDecimal rewardAmount = jackpot.getCurrentPool();
      Reward reward = new Reward(
          bet.getBetId(),
          jackpot.getJackpotId(),
          bet.getUserId(),
          rewardAmount);

      // Reset jackpot pool
      jackpot.setCurrentPool(jackpot.getInitialPool());
      jackpotRepository.save(jackpot);

      // Save reward
      rewardRepository.save(reward);

      return reward;
    }
    return null;
  }


}

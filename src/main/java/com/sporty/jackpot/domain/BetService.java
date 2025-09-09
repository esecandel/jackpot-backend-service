package com.sporty.jackpot.domain;

import com.github.dockerjava.api.exception.NotFoundException;
import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.Reward;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.domain.persistence.RewardRepository;
import com.sporty.jackpot.infra.api.model.RewardResponse;
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

  public RewardResponse checkReward(UUID betId) {
    Bet bet = betRepository.findById(betId)
        .orElseThrow(() -> new NotFoundException("Bet not found"));
    Jackpot jackpot = jackpotRepository.findById(bet.getJackpotId())
        .orElseThrow(() -> new NotFoundException("Jackpot not found"));

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

      return toWinResponse(reward);
    } else {
      return new RewardResponse(
          betId,
          bet.getJackpotId(),
          bet.getUserId(),
          BigDecimal.ZERO,
          null,
          "Sorry, no reward this time. Better luck next time!"
      );

    }
  }

  private RewardResponse toWinResponse(Reward reward) {
    return new RewardResponse(
        reward.getBetId(),
        reward.getJackpotId(),
        reward.getUserId(),
        reward.getAmount(),
        reward.getGrantedAt(),
        "Congratulations! You have won the jackpot!"
    );
  }


}

package com.sporty.jackpot.domain;

import com.github.dockerjava.api.exception.NotFoundException;
import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.Contribution;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.infra.messaging.events.BetMessage;
import java.math.BigDecimal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class JackpotService {

  private final Logger logger = LoggerFactory.getLogger(JackpotService.class);

  private final JackpotRepository jackpotRepository;
  private final BetRepository betRepository;

  public JackpotService(JackpotRepository jackpotRepository, BetRepository betRepository) {
    this.jackpotRepository = jackpotRepository;
    this.betRepository = betRepository;
  }

  public void processBet(BetMessage betMessage) {
    logger.info("Processing bet: {}", betMessage);

    try {
      Jackpot jackpot = jackpotRepository.findById(betMessage.jackpotId())
          .orElseThrow(() -> new NotFoundException("Jackpot not found"));

      if (jackpot.getContributions().stream()
          .anyMatch(c -> c.getBetId().equals(betMessage.betRequestId()))) {
        logger.warn("Bet with id {} has already been processed", betMessage.betRequestId());
        return;
      }

      Bet bet = new Bet(betMessage.betRequestId(), betMessage.userId(), betMessage.jackpotId(),
          betMessage.betAmount());
      betRepository.save(bet);
      logger.info("Bet saved: {}", bet);

      BigDecimal contributionAmount = ContributionCalculator.calculate(jackpot,
          betMessage.betAmount());
      logger.info("Calculated contribution amount: {}", contributionAmount);

      jackpot.addContribution(new Contribution(
          bet.getBetId(),
          contributionAmount));

      jackpotRepository.save(jackpot);
      logger.info("Jackpot updated: {}", jackpot);
    } catch (Exception e) {
      logger.error("Error processing bet", e);
      throw new RuntimeException(e);
    }
  }


}

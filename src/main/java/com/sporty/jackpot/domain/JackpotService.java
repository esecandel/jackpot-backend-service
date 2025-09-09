package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.Contribution;
import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.infra.messaging.events.BetMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class JackpotService {

  private final JackpotRepository jackpotRepository;
  private final BetRepository betRepository;

  public JackpotService(JackpotRepository jackpotRepository, BetRepository betRepository) {
    this.jackpotRepository = jackpotRepository;
    this.betRepository = betRepository;
  }

  public void processBet(BetMessage betMessage) {
    Jackpot jackpot = jackpotRepository.findById(betMessage.jackpotId())
        .orElseThrow(() -> new RuntimeException("Jackpot not found"));

    Bet bet = new Bet(betMessage.userId(), betMessage.jackpotId(), betMessage.betAmount());
    betRepository.save(bet);

    BigDecimal contributionAmount = ContributionCalculator.calculate(jackpot, betMessage.betAmount());

    jackpot.setCurrentPool(jackpot.getCurrentPool().add(contributionAmount));

    Contribution contribution = new Contribution(contributionAmount, LocalDateTime.now());
    jackpot.getContributions().add(contribution);

    jackpotRepository.save(jackpot);
  }





}

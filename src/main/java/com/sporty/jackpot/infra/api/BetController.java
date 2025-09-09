package com.sporty.jackpot.infra.api;

import com.sporty.jackpot.domain.BetService;
import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.infra.api.model.RewardResponse;
import com.sporty.jackpot.infra.messaging.BetProducer;
import com.sporty.jackpot.infra.messaging.events.BetMessage;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bets")
public class BetController {

  private final Logger logger = LoggerFactory.getLogger(BetController.class);

  private final BetProducer betProducer;
  private final BetService betService;

  public BetController(BetProducer betProducer, BetService betService) {
    this.betProducer = betProducer;
    this.betService = betService;
  }

  @PostMapping
  public ResponseEntity<Bet> publishBet(@RequestBody Bet bet) {
    logger.info("Publishing bet: {}", bet);

    BetMessage betMessage = new BetMessage(
        UUID.randomUUID(),
        bet.getUserId(),
        bet.getJackpotId(),
        bet.getBetAmount(),
        LocalDateTime.now()
    );

    betProducer.sendBet(betMessage);

    return ResponseEntity.ok(bet);
  }

  @GetMapping("/{betId}/reward")
  public ResponseEntity<RewardResponse> checkReward(@PathVariable UUID betId) {
    logger.info("Checking reward for bet: {}", betId);
    RewardResponse reward = betService.checkReward(betId);
    return ResponseEntity.ok(reward);
  }
}
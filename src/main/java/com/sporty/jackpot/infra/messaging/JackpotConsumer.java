package com.sporty.jackpot.infra.messaging;

import com.sporty.jackpot.domain.JackpotService;
import com.sporty.jackpot.infra.messaging.events.BetMessage;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JackpotConsumer {

  private final JackpotService jackpotService;

  public JackpotConsumer(JackpotService jackpotService) {
    this.jackpotService = jackpotService;
  }

  @KafkaListener(topics = "jackpot-bets", groupId = "jackpot-group")
  public void consumeBet(BetMessage betMessage) {
    jackpotService.processBet(betMessage);
  }
}
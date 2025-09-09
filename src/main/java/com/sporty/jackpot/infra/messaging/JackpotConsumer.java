package com.sporty.jackpot.infra.messaging;

import com.sporty.jackpot.domain.JackpotService;
import com.sporty.jackpot.infra.messaging.events.BetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class JackpotConsumer {

  private final Logger logger = LoggerFactory.getLogger(JackpotConsumer.class);

  private final JackpotService jackpotService;

  public JackpotConsumer(JackpotService jackpotService) {
    this.jackpotService = jackpotService;
  }

  @KafkaListener(topics = "jackpot-bets", groupId = "jackpot-group")
  public void consumeBet(BetMessage betMessage) {
    logger.info("Received BetMessage: {}", betMessage);
    jackpotService.processBet(betMessage);
  }
}
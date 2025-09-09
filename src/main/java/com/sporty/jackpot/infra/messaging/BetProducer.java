package com.sporty.jackpot.infra.messaging;

import com.sporty.jackpot.infra.messaging.events.BetMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BetProducer {

  private final Logger logger = LoggerFactory.getLogger(BetProducer.class);

  private final KafkaTemplate<String, BetMessage> kafkaTemplate;

  public BetProducer(KafkaTemplate<String, BetMessage> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendBet(BetMessage betMessage) {
    logger.info("Sending bet message: {}", betMessage);
    kafkaTemplate.send("jackpot-bets", betMessage);
  }
}

package com.sporty.jackpot.infra.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.persistence.BetRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;


class BetRepositoryTest {

  private final BetRepository betRepository = new BetRepositoryInMemory();

  @Test
  void saveBet_shouldPersistBetInDatabase() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID jackpotId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.50");
    Bet bet = new Bet(userId, jackpotId, betAmount);

    // Act
    betRepository.save(bet);
    Optional<Bet> savedOpt = betRepository.findById(bet.getBetId());
    assertThat(savedOpt).isPresent();
    Bet savedBet = savedOpt.get();

    // Assert
    assertThat(savedBet.getBetId()).isNotNull();
    assertThat(savedBet.getUserId()).isEqualTo(userId);
    assertThat(savedBet.getJackpotId()).isEqualTo(jackpotId);
    assertThat(savedBet.getBetAmount()).isEqualTo(betAmount);
    assertThat(savedBet.getCreatedAt()).isNotNull();
  }

  @Test
  void findById_shouldReturnBetIfExists() {
    // Arrange
    UUID userId = UUID.randomUUID();
    UUID jackpotId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("50.00");
    Bet bet = new Bet(userId, jackpotId, betAmount);
    betRepository.save(bet);

    // Act
    Optional<Bet> foundBet = betRepository.findById(bet.getBetId());

    // Assert
    assertThat(foundBet).isPresent();
    assertThat(foundBet.get().getBetId()).isEqualTo(bet.getBetId());
  }

  @Test
  void findAll_shouldReturnAllBets() {
    // Arrange
    Bet bet1 = new Bet(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("25.00"));
    Bet bet2 = new Bet(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("75.00"));

    betRepository.save(bet1);
    betRepository.save(bet2);

    // Act
    List<Bet> bets = betRepository.findAll();

    // Assert
    assertThat(bets).hasSize(2);
    assertThat(bets).extracting("betAmount")
        .containsExactlyInAnyOrder(new BigDecimal("25.00"), new BigDecimal("75.00"));
  }

  @Test
  void deleteById_shouldRemoveBetFromDatabase() {
    // Arrange
    Bet bet = new Bet(UUID.randomUUID(), UUID.randomUUID(), new BigDecimal("150.00"));
    betRepository.save(bet);

    // Act
    betRepository.delete(bet.getBetId());
    Optional<Bet> foundBet = betRepository.findById(bet.getBetId());

    // Assert
    assertThat(foundBet).isNotPresent();
  }
}
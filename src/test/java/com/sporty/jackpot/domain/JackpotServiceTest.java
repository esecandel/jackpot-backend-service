package com.sporty.jackpot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.infra.messaging.events.BetMessage;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class JackpotServiceTest {

  private JackpotRepository jackpotRepository;
  private BetRepository betRepository;
  private JackpotService jackpotService;

  @BeforeEach
  void setUp() {
    jackpotRepository = Mockito.mock(JackpotRepository.class);
    betRepository = Mockito.mock(BetRepository.class);
    jackpotService = new JackpotService(jackpotRepository, betRepository);
  }

  @Test
  void processBet_shouldCreateBetAndUpdateJackpot_withFixedContribution() {
    // Arrange
    UUID jackpotId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID betRequestId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.00");
    BigDecimal initialPool = new BigDecimal("1000.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.FIXED,
        RewardType.FIXED
    );
    jackpot.setJackpotId(jackpotId);

    BetMessage betMessage = new BetMessage(
        betRequestId,
        userId,
        jackpotId,
        betAmount,
        LocalDateTime.now()
    );

    when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.of(jackpot));

    // Act
    jackpotService.processBet(betMessage);

    // Assert
    // Verify bet was saved
    ArgumentCaptor<Bet> betCaptor = ArgumentCaptor.forClass(Bet.class);
    verify(betRepository).save(betCaptor.capture());
    Bet savedBet = betCaptor.getValue();

    assertThat(savedBet.getUserId()).isEqualTo(userId);
    assertThat(savedBet.getJackpotId()).isEqualTo(jackpotId);
    assertThat(savedBet.getBetAmount()).isEqualTo(betAmount);

    // Verify jackpot was updated and saved
    ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
    verify(jackpotRepository).save(jackpotCaptor.capture());
    Jackpot savedJackpot = jackpotCaptor.getValue();

    // For FIXED contribution type with 10% rate, contribution should be 10.00
    BigDecimal expectedContribution = new BigDecimal("10.00");
    BigDecimal expectedNewPool = initialPool.add(expectedContribution);

    assertThat(savedJackpot.getCurrentPool()).isEqualByComparingTo(expectedNewPool);
    assertThat(savedJackpot.getContributions()).hasSize(1);
    assertThat(savedJackpot.getContributions().get(0).getContributionAmount())
        .isEqualByComparingTo(expectedContribution);
  }

  @Test
  void processBet_shouldCreateBetAndUpdateJackpot_withVariableContribution() {
    // Arrange
    UUID jackpotId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID betRequestId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.00");
    BigDecimal initialPool = new BigDecimal("1000.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.VARIABLE,
        RewardType.FIXED
    );
    jackpot.setJackpotId(jackpotId);

    BetMessage betMessage = new BetMessage(
        betRequestId,
        userId,
        jackpotId,
        betAmount,
        LocalDateTime.now()
    );

    when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.of(jackpot));

    // Act
    jackpotService.processBet(betMessage);

    // Assert
    ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
    verify(jackpotRepository).save(jackpotCaptor.capture());
    Jackpot savedJackpot = jackpotCaptor.getValue();

    // For VARIABLE contribution type, the initial rate is 20% but decreases as pool grows
    // With pool ratio of 1.0, the adjusted percentage should be 20% - (1.0 * 5%) = 15%
    BigDecimal expectedContribution = new BigDecimal("15.00");
    BigDecimal expectedNewPool = initialPool.add(expectedContribution);

    assertThat(savedJackpot.getCurrentPool()).isEqualByComparingTo(expectedNewPool);
    assertThat(savedJackpot.getContributions()).hasSize(1);
    assertThat(savedJackpot.getContributions().get(0).getContributionAmount())
        .isEqualByComparingTo(expectedContribution);
  }

  @Test
  void processBet_shouldInitializeCurrentPool_whenCurrentPoolIsNull() {
    // Arrange
    UUID jackpotId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID betRequestId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.00");
    BigDecimal initialPool = new BigDecimal("1000.00");

    Jackpot jackpot = new Jackpot(
        "Test Jackpot",
        initialPool,
        ContributionType.FIXED,
        RewardType.FIXED
    );
    jackpot.setJackpotId(jackpotId);
    // Current pool is null

    BetMessage betMessage = new BetMessage(
        betRequestId,
        userId,
        jackpotId,
        betAmount,
        LocalDateTime.now()
    );

    when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.of(jackpot));

    // Act
    jackpotService.processBet(betMessage);

    // Assert
    ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
    verify(jackpotRepository).save(jackpotCaptor.capture());
    Jackpot savedJackpot = jackpotCaptor.getValue();

    // For FIXED contribution type with 10% rate, contribution should be 10.00
    BigDecimal expectedContribution = new BigDecimal("10.00");
    BigDecimal expectedNewPool = initialPool.add(expectedContribution);

    assertThat(savedJackpot.getCurrentPool()).isEqualByComparingTo(expectedNewPool);
  }

  @Test
  void processBet_shouldThrowException_whenJackpotNotFound() {
    // Arrange
    UUID nonExistentJackpotId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();
    UUID betRequestId = UUID.randomUUID();
    BigDecimal betAmount = new BigDecimal("100.00");

    BetMessage betMessage = new BetMessage(
        betRequestId,
        userId,
        nonExistentJackpotId,
        betAmount,
        LocalDateTime.now()
    );

    when(jackpotRepository.findById(nonExistentJackpotId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> jackpotService.processBet(betMessage))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Jackpot not found");

    // Verify no bet was saved
    verify(betRepository, never()).save(any(Bet.class));
  }
}

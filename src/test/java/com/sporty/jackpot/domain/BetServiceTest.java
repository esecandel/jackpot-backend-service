package com.sporty.jackpot.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.Reward;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.domain.persistence.BetRepository;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.domain.persistence.RewardRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BetServiceTest {

    @Mock
    private JackpotRepository jackpotRepository;
    
    @Mock
    private BetRepository betRepository;
    
    @Mock
    private RewardRepository rewardRepository;
    
    @Mock
    private RewardChecker rewardChecker;
    
    private BetService betService;
    
    @BeforeEach
    void setUp() {
        // Create a BetService with mocked dependencies
        betService = new BetService(jackpotRepository, betRepository, rewardRepository);
        
        // Replace the RewardChecker in BetService with our mock
        try {
            java.lang.reflect.Field field = BetService.class.getDeclaredField("rewardChecker");
            field.setAccessible(true);
            field.set(betService, rewardChecker);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock RewardChecker", e);
        }
    }
    
    @Test
    void checkReward_shouldReturnReward_whenBetWins() {
        // Arrange
        UUID betId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID jackpotId = UUID.randomUUID();
        BigDecimal betAmount = new BigDecimal("100.00");
        BigDecimal initialPool = new BigDecimal("1000.00");
        BigDecimal currentPool = new BigDecimal("2000.00");
        
        Bet bet = new Bet(userId, jackpotId, betAmount);
        bet.setBetId(betId);
        
        Jackpot jackpot = new Jackpot(
            "Test Jackpot",
            initialPool,
            ContributionType.FIXED,
            RewardType.FIXED
        );
        jackpot.setJackpotId(jackpotId);
        jackpot.setCurrentPool(currentPool);
        
        when(betRepository.findById(betId)).thenReturn(Optional.of(bet));
        when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.of(jackpot));
        when(rewardChecker.evaluate(jackpot)).thenReturn(true); // Bet wins
        
        // Act
        Reward reward = betService.checkReward(betId);
        
        // Assert
        assertThat(reward).isNotNull();
        assertThat(reward.getBetId()).isEqualTo(betId);
        assertThat(reward.getJackpotId()).isEqualTo(jackpotId);
        assertThat(reward.getUserId()).isEqualTo(userId);
        assertThat(reward.getAmount()).isEqualTo(currentPool);
        
        // Verify jackpot was reset and saved
        ArgumentCaptor<Jackpot> jackpotCaptor = ArgumentCaptor.forClass(Jackpot.class);
        verify(jackpotRepository).save(jackpotCaptor.capture());
        Jackpot savedJackpot = jackpotCaptor.getValue();
        assertThat(savedJackpot.getCurrentPool()).isEqualTo(initialPool);
        
        // Verify reward was saved
        ArgumentCaptor<Reward> rewardCaptor = ArgumentCaptor.forClass(Reward.class);
        verify(rewardRepository).save(rewardCaptor.capture());
        Reward savedReward = rewardCaptor.getValue();
        assertThat(savedReward).isEqualTo(reward);
    }
    
    @Test
    void checkReward_shouldReturnNull_whenBetDoesNotWin() {
        // Arrange
        UUID betId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID jackpotId = UUID.randomUUID();
        BigDecimal betAmount = new BigDecimal("100.00");
        BigDecimal initialPool = new BigDecimal("1000.00");
        BigDecimal currentPool = new BigDecimal("2000.00");
        
        Bet bet = new Bet(userId, jackpotId, betAmount);
        bet.setBetId(betId);
        
        Jackpot jackpot = new Jackpot(
            "Test Jackpot",
            initialPool,
            ContributionType.FIXED,
            RewardType.FIXED
        );
        jackpot.setJackpotId(jackpotId);
        jackpot.setCurrentPool(currentPool);
        
        when(betRepository.findById(betId)).thenReturn(Optional.of(bet));
        when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.of(jackpot));
        when(rewardChecker.evaluate(jackpot)).thenReturn(false); // Bet does not win
        
        // Act
        Reward reward = betService.checkReward(betId);
        
        // Assert
        assertThat(reward).isNull();
        
        // Verify jackpot was not modified or saved
        verify(jackpotRepository, never()).save(any(Jackpot.class));
        
        // Verify no reward was saved
        verify(rewardRepository, never()).save(any(Reward.class));
    }
    
    @Test
    void checkReward_shouldThrowException_whenBetNotFound() {
        // Arrange
        UUID nonExistentBetId = UUID.randomUUID();
        
        when(betRepository.findById(nonExistentBetId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> betService.checkReward(nonExistentBetId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Bet not found");
        
        // Verify no interactions with other repositories
        verify(jackpotRepository, never()).findById(any(UUID.class));
        verify(jackpotRepository, never()).save(any(Jackpot.class));
        verify(rewardRepository, never()).save(any(Reward.class));
    }
    
    @Test
    void checkReward_shouldThrowException_whenJackpotNotFound() {
        // Arrange
        UUID betId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID jackpotId = UUID.randomUUID();
        BigDecimal betAmount = new BigDecimal("100.00");
        
        Bet bet = new Bet(userId, jackpotId, betAmount);
        bet.setBetId(betId);
        
        when(betRepository.findById(betId)).thenReturn(Optional.of(bet));
        when(jackpotRepository.findById(jackpotId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThatThrownBy(() -> betService.checkReward(betId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Jackpot not found");
        
        // Verify no interactions with reward repository
        verify(rewardRepository, never()).save(any(Reward.class));
    }
}
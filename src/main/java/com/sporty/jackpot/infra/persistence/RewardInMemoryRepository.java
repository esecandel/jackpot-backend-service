package com.sporty.jackpot.infra.persistence;

import com.sporty.jackpot.domain.model.Reward;
import com.sporty.jackpot.domain.persistence.RewardRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class RewardInMemoryRepository implements RewardRepository {

  private final Map<UUID, Reward> storage = new HashMap<>();

  @Override
  public void save(Reward reward) {
     storage.put(reward.getRewardId(), reward);
  }

  @Override
  public Optional<Reward> findById(UUID betId) {
    return storage.containsKey(betId) ? Optional.of(storage.get(betId)) : Optional.empty();
  }

  @Override
  public List<Reward> findAll(){
    return storage.values().stream().toList();
  }
}

package com.sporty.jackpot.domain.persistence;


import com.sporty.jackpot.domain.model.Reward;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RewardRepository {

  void save(Reward entity);

  Optional<Reward> findById(UUID betId);

  List<Reward> findAll();
}

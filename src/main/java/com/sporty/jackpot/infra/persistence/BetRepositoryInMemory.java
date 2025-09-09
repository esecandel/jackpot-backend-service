package com.sporty.jackpot.infra.persistence;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.persistence.BetRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class BetRepositoryInMemory implements BetRepository {

  private final Map<UUID, Bet> storage = new HashMap<>();

  @Override
  public List<Bet> findByJackpotId(UUID jackpotId) {
    return findAll().stream()
        .filter(bet -> bet.getJackpotId().equals(jackpotId))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<Bet> findById(UUID betId) {
    return storage.containsKey(betId) ? Optional.of(storage.get(betId)) : Optional.empty();
  }

  @Override
  public List<Bet> findAll(){
    return storage.values().stream().toList();
  }

  @Override
  public void save(Bet bet) {
    storage.put(bet.getBetId(), bet);
  }

  @Override
  public void delete(UUID betId) {
    storage.remove(betId);
  }
}

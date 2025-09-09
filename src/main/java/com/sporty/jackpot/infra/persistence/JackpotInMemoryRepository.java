package com.sporty.jackpot.infra.persistence;

import com.sporty.jackpot.domain.model.Bet;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JackpotInMemoryRepository implements JackpotRepository {

  private final Map<UUID, Jackpot> storage = new HashMap<>();

  @Override
  public Optional<Jackpot> findById(UUID jackpotId) {
    return storage.containsKey(jackpotId) ? Optional.of(storage.get(jackpotId)) : Optional.empty();
  }

  @Override
  public List<Jackpot> findAll() {
    return storage.values().stream().toList();
  }

  @Override
  public void save(Jackpot jackpot) {
     storage.put(jackpot.getJackpotId(), jackpot);
  }

  @Override
  public void delete(UUID jackpotId) {
    storage.remove(jackpotId);
  }
}

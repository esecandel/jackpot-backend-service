package com.sporty.jackpot.infra.persistence;

import com.sporty.jackpot.domain.model.ContributionType;
import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.model.RewardType;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JackpotInMemoryRepository implements JackpotRepository {

  private final Map<UUID, Jackpot> storage = new HashMap<>();

  public JackpotInMemoryRepository() {
    UUID jackpotId1 = UUID.fromString("73cada80-12e4-46b7-a0cb-a5eb99d4cafa");
    storage.put(jackpotId1, new Jackpot(
        jackpotId1,
            "Super Fixed Jackpot",
            BigDecimal.valueOf(1000.0),
            BigDecimal.valueOf(1000.0),
            LocalDateTime.now(),
            ContributionType.FIXED,
            RewardType.FIXED,
            new ArrayList<>()
        )
    );
    UUID jackpotId2 = UUID.fromString("38b36029-44ea-4b38-b8cf-db64200dec3d");
    storage.put(jackpotId2, new Jackpot(
        jackpotId2,
        "Super Variable Jackpot",
        BigDecimal.valueOf(2000.0),
        BigDecimal.valueOf(2000.0),
        LocalDateTime.now(),
        ContributionType.VARIABLE,
        RewardType.VARIABLE,
        new ArrayList<>()
    ));

  }

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

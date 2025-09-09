package com.sporty.jackpot.domain.persistence;

import com.sporty.jackpot.domain.model.Bet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

public interface BetRepository {

  List<Bet> findByJackpotId(UUID jackpotId);

  Optional<Bet> findById(UUID betId);

  List<Bet> findAll();

  void save(Bet bet);

  void delete(UUID betId);
}

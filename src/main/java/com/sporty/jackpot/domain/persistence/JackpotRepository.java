package com.sporty.jackpot.domain.persistence;


import com.sporty.jackpot.domain.model.Jackpot;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JackpotRepository {

  Optional<Jackpot> findById(UUID jackpotId);

  List<Jackpot> findAll();

  void save(Jackpot jackpot);

  void delete(UUID jackpotId);
}


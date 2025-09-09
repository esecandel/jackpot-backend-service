package com.sporty.jackpot.domain;

import com.sporty.jackpot.domain.model.Jackpot;
import com.sporty.jackpot.domain.persistence.JackpotRepository;
import com.sporty.jackpot.infra.api.model.JackpotRequest;
import com.sporty.jackpot.infra.api.model.JackpotResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class JackpotCrudService {

  private final JackpotRepository repository;

  public JackpotCrudService(JackpotRepository repository) {
    this.repository = repository;
  }

  public JackpotResponse create(JackpotRequest request) {
    Jackpot jackpot = new Jackpot(
        request.name(),
        request.initialPool(),
        request.contributionType(),
        request.rewardType()
    );
    repository.save(jackpot);
    return toResponse(jackpot);
  }

  public List<JackpotResponse> getAll() {
    return repository.findAll().stream()
        .map(this::toResponse)
        .toList();
  }

  public JackpotResponse getById(UUID id) {
    Jackpot jackpot = repository.findById(id)
        .orElseThrow(() -> new RuntimeException("Jackpot not found"));
    return toResponse(jackpot);
  }


  public void delete(UUID id) {
    repository.delete(id);
  }

  private JackpotResponse toResponse(Jackpot jackpot) {
    return new JackpotResponse(
        jackpot.getJackpotId(),
        jackpot.getName(),
        jackpot.getInitialPool(),
        jackpot.getCurrentPool(),
        jackpot.getContributionType(),
        jackpot.getRewardType(),
        jackpot.getCreatedAt()
    );
  }
}

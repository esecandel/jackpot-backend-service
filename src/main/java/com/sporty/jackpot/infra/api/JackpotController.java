package com.sporty.jackpot.infra.api;


import com.sporty.jackpot.domain.JackpotCrudService;
import com.sporty.jackpot.infra.api.model.JackpotRequest;
import com.sporty.jackpot.infra.api.model.JackpotResponse;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jackpots")
public class JackpotController {

  private final JackpotCrudService jackpotCrudService;

  public JackpotController(JackpotCrudService jackpotCrudService) {
    this.jackpotCrudService = jackpotCrudService;
  }

  @PostMapping
  public ResponseEntity<JackpotResponse> create(@RequestBody JackpotRequest req) {
    JackpotResponse response = jackpotCrudService.create(req);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping
  public ResponseEntity<List<JackpotResponse>> getAll() {
    List<JackpotResponse> response = jackpotCrudService.getAll();
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{jackpotId}")
  public ResponseEntity<JackpotResponse> getById(@PathVariable UUID jackpotId) {
    JackpotResponse response = jackpotCrudService.getById(jackpotId);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{jackpotId}")
  public ResponseEntity<JackpotResponse> deleteById(@PathVariable UUID jackpotId) {
    jackpotCrudService.delete(jackpotId);
    return ResponseEntity.ok().build();
  }
}

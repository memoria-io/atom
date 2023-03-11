package io.memoria.reactive.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ESRepo {
  Mono<ESRepoRow> getFirst(String table, String stateId);

  Flux<ESRepoRow> getAll(String table, String stateId);

  Flux<ESRepoRow> getAll(String table, String stateId, int minSeqId);

  Mono<ESRepoRow> append(ESRepoRow esRepoRow);

  static ESRepo inMemory(List<String> topicNames) {
    return new MemESRepo(topicNames);
  }

  static ESRepo inMemory(String... topicNames) {
    return new MemESRepo(topicNames);
  }
}

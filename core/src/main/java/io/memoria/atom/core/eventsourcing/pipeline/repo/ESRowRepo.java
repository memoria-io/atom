package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ESRowRepo {
  Flux<ESRow> getAll(String table, String stateId);
  Mono<ESRow> append(String table, String stateId, String value);

  static ESRowRepo inMemory(List<String> topicNames) {
    return new MemESRowRepo(topicNames);
  }

  static ESRowRepo inMemory(String... topicNames) {
    return new MemESRowRepo(topicNames);
  }
}

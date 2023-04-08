package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;

public interface ESRowRepo {
  Flux<ESRow> getAll(String table, String stateId);

  Flux<ESRow> append(String table, Flux<ESRow> values);

  static ESRowRepo inMemory(List<String> topicNames) {
    return new MemESRowRepo(topicNames);
  }

  static ESRowRepo inMemory(String... topicNames) {
    return new MemESRowRepo(topicNames);
  }
}

package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.vavr.collection.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MemESRowRepo implements ESRowRepo {
  private final Map<String, java.util.List<ESRow>> topics = new ConcurrentHashMap<>();

  public MemESRowRepo(List<String> topicNames) {
    topicNames.forEach(this::putTopic);
  }

  public MemESRowRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(this::putTopic);
  }

  @Override
  public Flux<ESRow> getAll(String table, String stateId) {
    return Flux.fromIterable(this.topics.get(table)).filter(msg -> msg.stateId().equals(stateId));
  }

  @Override
  public Mono<ESRow> append(String table, String stateId, String value) {
    return Mono.fromCallable(() -> {
      this.topics.computeIfPresent(table, (k, v) -> {
        var seqId = (int) v.stream().filter(r -> r.stateId().equals(stateId)).count();
        v.add(new ESRow(table, stateId, seqId, value));
        return v;
      });
      this.topics.computeIfAbsent(table, k -> {
        var arr = new ArrayList<ESRow>();
        arr.add(new ESRow(table, stateId, 0, value));
        return arr;
      });
      return new ESRow(table, stateId, 0, value);
    });
  }

  private void putTopic(String topicName) {
    this.topics.put(topicName, new ArrayList<>());
  }
}

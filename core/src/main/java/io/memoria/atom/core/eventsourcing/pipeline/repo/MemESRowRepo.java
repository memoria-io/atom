package io.memoria.atom.core.eventsourcing.pipeline.repo;

import io.memoria.atom.core.eventsourcing.exception.ESException.MismatchingEventSeqId;
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
  public Flux<ESRow> append(String table, Flux<ESRow> rows) {
    return rows.concatMap(r -> append(table, r));
  }

  private Mono<ESRow> append(String table, ESRow row) {
    return Mono.fromCallable(() -> {
      var esRows = this.topics.get(table);
      if (esRows != null) {
        if (esRows.stream().anyMatch(r -> r.stateId().equals(row.stateId()) && r.seqId() == row.seqId())) {
          throw MismatchingEventSeqId.of(row.seqId());
        }
        esRows.add(row);
      } else {
        var rows = new ArrayList<ESRow>();
        rows.add(row);
        this.topics.put(table, rows);
      }
      return row;
    });
  }

  private void putTopic(String topicName) {
    this.topics.put(topicName, new ArrayList<>());
  }
}

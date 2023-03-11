package io.memoria.reactive.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

class MemESRepo implements ESRepo {
  private final Map<String, List<ESRepoRow>> topics = new ConcurrentHashMap<>();

  public MemESRepo(List<String> topicNames) {
    topicNames.forEach(this::putTopic);
  }

  public MemESRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(this::putTopic);
  }

  @Override
  public Mono<ESRepoRow> getFirst(String table, String stateId) {
    var list = this.topics.get(table);
    if (list.isEmpty()) {
      return Mono.empty();
    } else {
      return Mono.just(list.get(0));
    }
  }

  @Override
  public Flux<ESRepoRow> getAll(String table, String stateId) {
    return Flux.fromIterable(this.topics.get(table)).filter(msg -> msg.stateId().equals(stateId));
  }

  @Override
  public Flux<ESRepoRow> getAll(String table, String stateId, int startIdx) {
    return Flux.fromIterable(this.topics.get(table))
               .filter(msg -> msg.stateId().equals(stateId))
               .filter(msg -> msg.seqId() >= startIdx);
  }

  @Override
  public Mono<ESRepoRow> append(ESRepoRow r) {
    return Mono.just(appendIfPresent(r));
  }

  private ESRepoRow appendIfPresent(ESRepoRow r) {
    topics.computeIfPresent(r.table(), (k, v) -> {
      v.add(r);
      return v;
    });
    return r;
  }

  private void putTopic(String topicName) {
    this.topics.put(topicName, new ArrayList<>());
  }
}

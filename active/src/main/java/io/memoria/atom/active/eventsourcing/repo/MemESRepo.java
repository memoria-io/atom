package io.memoria.atom.active.eventsourcing.repo;

import io.vavr.control.Try;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

class MemESRepo implements ESRepo {
  private final Map<String, List<ESRepoRow>> topics = new ConcurrentHashMap<>();

  public MemESRepo(List<String> topicNames) {
    topicNames.forEach(this::putTopic);
  }

  public MemESRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(this::putTopic);
  }

  @Override
  public Stream<ESRepoRow> getAll(String table, String stateId) {
    return List.copyOf(this.topics.get(table)).stream().filter(msg -> msg.stateId().equals(stateId));
  }

  @Override
  public Stream<ESRepoRow> getAll(String table, String stateId, int startIdx) {
    return List.copyOf(this.topics.get(table))
               .stream()
               .filter(msg -> msg.stateId().equals(stateId))
               .filter(msg -> msg.seqId() >= startIdx);
  }

  @Override
  public Try<ESRepoRow> append(ESRepoRow r) {
    return Try.of(() -> {
      topics.computeIfPresent(r.table(), (k, v) -> {
        v.add(r);
        return v;
      });
      return r;
    });
  }

  private void putTopic(String topicName) {
    this.topics.put(topicName, new ArrayList<>());
  }
}

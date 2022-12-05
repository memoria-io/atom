package io.memoria.atom.active.eventsourcing.repo;

import io.vavr.control.Try;

import java.util.*;
import java.util.stream.Stream;

class MemESRepo implements ESRepo {
  private final Map<String, List<ESRepoRow>> topics = new HashMap<>();

  public MemESRepo(List<String> topicNames) {
    topicNames.forEach(this::putTopic);
  }

  public MemESRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(this::putTopic);
  }

  @Override
  public Stream<ESRepoRow> getAll(String table, String stateId) {
    return this.topics.get(table).stream().filter(msg -> msg.stateId().equals(stateId));
  }

  @Override
  public Try<ESRepoRow> append(ESRepoRow r) {
    return Try.of(() -> {
      topics.get(r.table()).add(r);
      return r;
    });
  }

  private void putTopic(String topicName) {
    this.topics.put(topicName, new ArrayList<>());
  }
}

package io.memoria.atom.active.eventsourcing.repo;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

class MemEventRepo<E extends Event> implements EventRepo<E> {
  private final Map<String, List<E>> topics = new ConcurrentHashMap<>();

  public MemEventRepo(List<String> topicNames) {
    topicNames.forEach(this::putTopic);
  }

  public MemEventRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(this::putTopic);
  }

  @Override
  public Stream<Try<E>> getAll(String topic, StateId stateId) {
    return List.copyOf(this.topics.get(topic)).stream().filter(msg -> msg.stateId().equals(stateId)).map(Try::success);
  }

  @Override
  public Stream<Try<E>> getAll(String topic, StateId stateId, int startIdx) {
    return List.copyOf(this.topics.get(topic))
               .stream()
               .filter(msg -> msg.stateId().equals(stateId))
               .skip(startIdx)
               .map(Try::success);
  }

  @Override
  public Try<Integer> append(String topic, int seqId, E e) {
    return Try.of(() -> {
      topics.computeIfPresent(topic, (k, v) -> {
        v.add(e);
        return v;
      });
      return seqId;
    });
  }

  private List<E> putTopic(String topicName) {
    return this.topics.put(topicName, new ArrayList<>());
  }
}

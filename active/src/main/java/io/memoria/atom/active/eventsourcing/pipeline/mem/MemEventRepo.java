package io.memoria.atom.active.eventsourcing.pipeline.mem;

import io.memoria.atom.active.eventsourcing.pipeline.EventRepo;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.*;
import java.util.stream.Stream;

public class MemEventRepo<E extends Event> implements EventRepo<E> {
  private final Map<String, List<E>> topics = new HashMap<>();

  public MemEventRepo(List<String> topicNames) {
    topicNames.forEach(name -> this.topics.put(name, new ArrayList<>()));
  }

  public MemEventRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(name -> this.topics.put(name, new ArrayList<>()));
  }

  @Override
  public Stream<Try<E>> getAll(String topic, StateId stateId) {
    return this.topics.get(topic).stream().filter(msg -> msg.stateId().equals(stateId)).map(Try::success);
  }

  @Override
  public Try<Integer> append(String topic, int seqId, E e) {
    return Try.of(() -> {
      topics.get(topic).add(e);
      return seqId;
    });
  }
}

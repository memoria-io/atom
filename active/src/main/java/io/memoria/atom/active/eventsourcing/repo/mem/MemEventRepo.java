package io.memoria.atom.active.eventsourcing.repo.mem;

import io.memoria.atom.active.eventsourcing.repo.EventMsg;
import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class MemEventRepo implements EventRepo {
  private final Map<String, List<EventMsg>> topics = new HashMap<>();

  public MemEventRepo(String... topicNames) {
    Arrays.stream(topicNames).forEach(name -> this.topics.put(name, new ArrayList<>()));
  }

  @Override
  public Stream<EventMsg> getAll(String topic, StateId stateId) {
    return this.topics.get(topic).stream().filter(msg -> msg.stateId().equals(stateId));
  }

  @Override
  public Try<Integer> append(EventMsg event) {
    return Try.of(() -> {
      topics.get(event.topic()).add(event);
      return event.seqId();
    });
  }
}

package io.memoria.atom.active.eventsourcing.repo.mem;

import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.Shardable;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MemEventRepo<E extends Event> implements EventRepo<E> {
  public final List<ArrayList<E>> topic;

  public MemEventRepo(int totalPartitions) {
    this.topic = IntStream.range(0, totalPartitions).mapToObj(i -> new ArrayList<E>()).toList();
  }

  @Override
  public Stream<Try<E>> getAll(StateId stateId) {
    var partition = Shardable.partition(stateId, topic.size());
    return topic.get(partition).stream().filter(msg -> msg.stateId().equals(stateId)).map(m -> Try.of(() -> m));
  }

  @Override
  public Try<E> append(E event) {
    return Try.of(() -> {
      int partition = event.partition(topic.size());
      topic.get(partition).add(event);
      return event;
    });
  }
}

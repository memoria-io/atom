package io.memoria.atom.es.active.repo.mem;

import io.memoria.atom.es.active.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.Shardable;
import io.memoria.atom.core.eventsourcing.StateId;
import io.vavr.control.Try;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MemEventRepo<E extends Event> implements EventRepo<E> {
  public final List<MemEventPartition<E>> topic;

  public MemEventRepo(List<MemEventPartition<E>> topic) {
    this.topic = topic;
  }

  public MemEventRepo(int totalPartitions) {
    this.topic = IntStream.range(0, totalPartitions).mapToObj(i -> new MemEventPartition<E>()).toList();
  }

  @Override
  public Stream<Try<E>> get(StateId stateId) {
    var partition = Shardable.partition(stateId, topic.size());
    return topic.get(partition).msgs().stream().filter(msg -> msg.stateId().equals(stateId)).map(m -> Try.of(() -> m));
  }

  @Override
  public Try<E> push(E event) {
    return Try.of(() -> {
      int partition = event.partition(topic.size());
      topic.get(partition).msgs().add(event);
      return event;
    });
  }
}

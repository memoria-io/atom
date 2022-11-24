package io.memoria.atom.active.eventsourcing.repo.mem;

import io.memoria.atom.active.eventsourcing.repo.CommandStream;
import io.memoria.atom.core.eventsourcing.Command;
import io.vavr.control.Try;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MemCommandStream<C extends Command> implements CommandStream<C> {
  public final List<LinkedBlockingDeque<C>> topic;
  private final int partition;

  public MemCommandStream(int partition, List<LinkedBlockingDeque<C>> topic) {
    this.topic = topic;
    this.partition = partition;
  }

  public MemCommandStream(int partition, int totalPartitions) {
    this.topic = IntStream.range(0, totalPartitions).mapToObj(i -> new LinkedBlockingDeque<C>()).toList();
    this.partition = partition;
  }

  @Override
  public Stream<Try<C>> stream() {
    var q = topic.get(partition);
    return Stream.generate(() -> {
      try {
        return Try.success(q.take());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public Try<C> send(C cmd) {
    return Try.of(() -> {
      int partition = cmd.partition(topic.size());
      topic.get(partition).offer(cmd);
      return cmd;
    });
  }
}
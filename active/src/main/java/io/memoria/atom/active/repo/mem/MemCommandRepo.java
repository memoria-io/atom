package io.memoria.atom.active.repo.mem;

import io.memoria.atom.active.repo.CommandRepo;
import io.memoria.atom.core.eventsourcing.Command;
import io.vavr.control.Try;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MemCommandRepo<C extends Command> implements CommandRepo<C> {
  public final List<MemCommandPartition<C>> topic;
  private final int partition;

  public MemCommandRepo(int partition, List<MemCommandPartition<C>> topic) {
    this.topic = topic;
    this.partition = partition;
  }

  public MemCommandRepo(int partition, int totalPartitions) {
    this.topic = IntStream.range(0, totalPartitions).mapToObj(i -> new MemCommandPartition<C>()).toList();
    this.partition = partition;
  }

  @Override
  public Stream<Try<C>> stream() {
    var q = topic.get(partition).msgs();
    return Stream.generate(() -> {
      try {
        return Try.success(q.take());
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  @Override
  public Try<C> push(C cmd) {
    return Try.of(() -> {
      int partition = cmd.partition(topic.size());
      topic.get(partition).msgs().offer(cmd);
      return cmd;
    });
  }
}
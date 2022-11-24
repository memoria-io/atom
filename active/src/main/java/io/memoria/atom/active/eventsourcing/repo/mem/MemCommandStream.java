package io.memoria.atom.active.eventsourcing.repo.mem;

import io.memoria.atom.active.eventsourcing.repo.CmdMsg;
import io.memoria.atom.active.eventsourcing.repo.CommandStream;
import io.vavr.control.Try;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class MemCommandStream implements CommandStream {
  private final Map<String, List<LinkedBlockingDeque<CmdMsg>>> topics = new HashMap<>();

  public MemCommandStream(Map<String, Integer> topicPartitions) {
    topicPartitions.forEach((key, value) -> this.topics.put(key, createTopic(value)));
  }

  public MemCommandStream(String topic, int totalPartitions) {
    this(Map.of(topic, totalPartitions));
  }

  @Override
  public Try<CmdMsg> pub(CmdMsg cmd) {
    return Try.of(() -> {
      topics.get(cmd.topic()).get(cmd.partition()).offer(cmd);
      return cmd;
    });
  }

  @Override
  public Stream<CmdMsg> sub(String topic, int partition) {
    var q = topics.get(topic).get(partition);
    return Stream.generate(() -> {
      try {
        return q.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private static List<LinkedBlockingDeque<CmdMsg>> createTopic(int e) {
    return IntStream.range(0, e).mapToObj(i -> new LinkedBlockingDeque<CmdMsg>()).toList();
  }
}
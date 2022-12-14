package io.memoria.atom.active.eventsourcing.stream;

import io.memoria.atom.core.eventsourcing.Command;
import io.vavr.control.Try;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class MemCommandStream<C extends Command> implements CommandStream<C> {
  private final Map<String, List<LinkedBlockingDeque<C>>> topics = new ConcurrentHashMap<>();

  public MemCommandStream(Map<String, Integer> topicPartitions) {
    topicPartitions.forEach((key, value) -> this.topics.put(key, createTopic(value)));
  }

  public MemCommandStream(String topic, int totalPartitions) {
    this(Map.of(topic, totalPartitions));
  }

  @Override
  public Try<C> pub(String topic, int partition, C c) {
    return Try.of(() -> {
      topics.get(topic).get(partition).offer(c);
      return c;
    });
  }

  @Override
  public Stream<Try<C>> sub(String topic, int partition) {
    var q = topics.get(topic).get(partition);
    return Stream.generate(() -> Try.of(q::take));
  }

  private List<LinkedBlockingDeque<C>> createTopic(int e) {
    return IntStream.range(0, e).mapToObj(i -> new LinkedBlockingDeque<C>()).toList();
  }
}

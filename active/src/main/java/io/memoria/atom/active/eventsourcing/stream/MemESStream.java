package io.memoria.atom.active.eventsourcing.stream;

import io.vavr.control.Try;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class MemESStream implements ESStream {
  private final Map<String, List<LinkedBlockingDeque<ESStreamMsg>>> topics = new ConcurrentHashMap<>();

  public MemESStream(Map<String, Integer> topicPartitions) {
    topicPartitions.forEach((key, value) -> this.topics.put(key, createTopic(value)));
  }

  public MemESStream(String topic, int totalPartitions) {
    this(Map.of(topic, totalPartitions));
  }

  @Override
  public Try<ESStreamMsg> pub(ESStreamMsg msg) {
    return Try.of(() -> {
      topics.get(msg.topic()).get(msg.partition()).offer(msg);
      return msg;
    });
  }

  @Override
  public Stream<ESStreamMsg> sub(String topic, int partition) {
    var q = topics.get(topic).get(partition);
    return Stream.generate(() -> {
      try {
        return q.take();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    });
  }

  private List<LinkedBlockingDeque<ESStreamMsg>> createTopic(int e) {
    return IntStream.range(0, e).mapToObj(i -> new LinkedBlockingDeque<ESStreamMsg>()).toList();
  }
}

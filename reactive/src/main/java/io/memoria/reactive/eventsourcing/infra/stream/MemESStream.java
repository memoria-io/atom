package io.memoria.reactive.eventsourcing.infra.stream;

import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public final class MemESStream implements ESStream {
  private final Map<String, List<LinkedBlockingDeque<ESStreamMsg>>> topics = new ConcurrentHashMap<>();

  public MemESStream(Map<String, Integer> topicPartitions) {
    topicPartitions.forEach((key, value) -> this.topics.put(key, createTopic(value)));
  }

  public MemESStream(String topic, int totalPartitions) {
    this(Map.of(topic, totalPartitions));
  }

  @Override
  public Mono<ESStreamMsg> pub(ESStreamMsg msg) {
    return Mono.fromCallable(() -> {
      topics.get(msg.topic()).get(msg.partition()).offer(msg);
      return msg;
    });
  }

  @Override
  public Flux<ESStreamMsg> sub(String topic, int partition) {
    var q = topics.get(topic).get(partition);
    return Flux.generate(c -> {
      try {
        c.next(q.take());
      } catch (InterruptedException e) {
        c.error(e);
      }
    });
  }

  private List<LinkedBlockingDeque<ESStreamMsg>> createTopic(int e) {
    return IntStream.range(0, e).mapToObj(i -> new LinkedBlockingDeque<ESStreamMsg>()).toList();
  }
}

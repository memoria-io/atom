package io.memoria.atom.core.stream;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.IntStream;

public final class MemESMsgStream implements ESMsgStream {
  private final Map<String, List<LinkedBlockingDeque<ESMsg>>> topics = new ConcurrentHashMap<>();

  public MemESMsgStream(io.vavr.collection.Map<String, Integer> topicPartitions) {
    topicPartitions.forEach((key, value) -> this.topics.put(key, createTopic(value)));
  }

  public MemESMsgStream(String topic, int totalPartitions) {
    this(HashMap.of(topic, totalPartitions));
  }

  @Override
  public Mono<ESMsg> pub(ESMsg msg) {
    return Mono.fromCallable(() -> {
      topics.get(msg.topic()).get(msg.partition()).offer(msg);
      return msg;
    });
  }

  @Override
  public Flux<ESMsg> sub(String topic, int partition) {
    var q = topics.get(topic).get(partition);
    return Flux.generate(c -> {
      try {
        c.next(q.take());
      } catch (InterruptedException e) {
        c.error(e);
      }
    });
  }

  @Override
  public void close() {
    // Silence is golden
  }

  private List<LinkedBlockingDeque<ESMsg>> createTopic(int e) {
    return IntStream.range(0, e).mapToObj(i -> new LinkedBlockingDeque<ESMsg>()).toList();
  }
}

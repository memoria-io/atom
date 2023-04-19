package io.memoria.atom.core.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.Many;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

public final class MemESMsgStream implements ESMsgStream {
  private final Map<String, List<Many<ESMsg>>> topics = new ConcurrentHashMap<>();

  public MemESMsgStream(Map<String, Integer> topics) {
    this(topics, Integer.MAX_VALUE);
  }

  public MemESMsgStream(Map<String, Integer> topics, int capacity) {
    topics.forEach((k, v) -> setup(k, v, capacity));
  }

  @Override
  public Mono<ESMsg> pub(ESMsg msg) {
    return Mono.fromCallable(() -> this.publishFn(msg));
  }

  @Override
  public Flux<ESMsg> sub(String topic, int partition) {
    return this.topics.get(topic).get(partition).asFlux();
  }

  private void setup(String topic, int nPartitions, int history) {
    var partitions = IntStream.range(0, nPartitions)
                              .mapToObj(i -> Sinks.many().replay().<ESMsg>limit(history))
                              .toList();
    this.topics.put(topic, partitions);
  }

  private ESMsg publishFn(ESMsg msg) {
    String topic = msg.topic();
    int partition = msg.partition();
    this.topics.get(topic).get(partition).tryEmitNext(msg);
    return msg;
  }
}

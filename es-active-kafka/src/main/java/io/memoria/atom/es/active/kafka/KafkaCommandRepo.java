package io.memoria.atom.es.active.kafka;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.es.active.kafka.client.KafkaClient;
import io.memoria.atom.es.active.kafka.client.Msg;
import io.memoria.atom.es.active.repo.CommandRepo;
import io.vavr.control.Try;

import java.util.stream.Stream;

public class KafkaCommandRepo<C extends Command> implements CommandRepo<C> {
  private final String topic;
  private final int streamPartition;
  private final int totalPartitions;
  private final Class<C> cClass;
  private final TextTransformer transformer;
  private final KafkaClient client;

  public KafkaCommandRepo(String topic,
                          int partition,
                          int totalPartitions,
                          Class<C> cClass,
                          TextTransformer transformer,
                          KafkaClient client) {
    this.topic = topic;
    this.streamPartition = partition;
    this.totalPartitions = totalPartitions;
    this.cClass = cClass;
    this.transformer = transformer;
    this.client = client;
  }

  @Override
  public Stream<Try<C>> stream() {
    return client.stream(topic, streamPartition).map(this::toCommand);
  }

  @Override
  public Try<C> push(C cmd) {
    var partition = cmd.partition(totalPartitions);
    var key = cmd.commandId().value();
    return transformer.serialize(cmd)
                      .map(body -> new Msg(topic, partition, key, body))
                      .flatMap(client::push)
                      .map(msg -> cmd);
  }

  private Try<C> toCommand(Msg msg) {
    return transformer.deserialize(msg.body(), cClass);
  }
}

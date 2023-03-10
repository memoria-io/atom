package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.reactive.eventsourcing.repo.Msg;
import io.vavr.collection.Map;
import org.apache.kafka.common.TopicPartition;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;
import reactor.kafka.sender.*;

import java.util.function.Supplier;

import static java.util.Collections.singleton;

class DefaultKafkaStream implements KafkaStream {
  public final Map<String, Object> producerConfig;
  public final Map<String, Object> consumerConfig;
  private final Supplier<Long> timeSupplier;

  DefaultKafkaStream(Map<String, Object> producerConfig,
                     Map<String, Object> consumerConfig,
                     Supplier<Long> timeSupplier) {
    this.producerConfig = producerConfig;
    this.consumerConfig = consumerConfig;
    this.timeSupplier = timeSupplier;
  }

  @Override
  public Flux<Msg> publish(Flux<Msg> msgs) {
    var records = msgs.map(this::toRecord);
    return createSender().send(records).map(SenderResult::correlationMetadata);
  }

  @Override
  public Mono<Long> size(String topic, int partition) {
    return Mono.fromCallable(() -> KafkaUtils.topicSize(topic, partition, consumerConfig));
  }

  @Override
  public Flux<Msg> subscribe(String topic, int partition, long offset) {
    return receive(topic, partition, offset).map(KafkaUtils::toMsg);
  }

  private Flux<ReceiverRecord<String, String>> receive(String topic, int partition, long offset) {
    var tp = new TopicPartition(topic, partition);
    var receiverOptions = ReceiverOptions.<String, String>create(consumerConfig.toJavaMap())
                                         .subscription(singleton(topic))
                                         .addAssignListener(partitions -> partitions.forEach(p -> p.seek(offset)))
                                         .assignment(singleton(tp));
    return KafkaReceiver.create(receiverOptions).receive();
  }

  private KafkaSender<String, String> createSender() {
    var senderOptions = SenderOptions.<String, String>create(producerConfig.toJavaMap());
    return KafkaSender.create(senderOptions);
  }

  private SenderRecord<String, String, Msg> toRecord(Msg msg) {
    return SenderRecord.create(msg.topic(), msg.partition(), timeSupplier.get(), msg.id().value(), msg.value(), msg);
  }
}

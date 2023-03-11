package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
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

class DefaultKafkaESStream implements KafkaESStream {
  public final Map<String, Object> producerConfig;
  public final Map<String, Object> consumerConfig;
  private final Supplier<Long> timeSupplier;
  private final KafkaSender<String, String> sender;

  DefaultKafkaESStream(Map<String, Object> producerConfig,
                       Map<String, Object> consumerConfig,
                       Supplier<Long> timeSupplier) {
    this.producerConfig = producerConfig;
    this.consumerConfig = consumerConfig;
    this.timeSupplier = timeSupplier;
    this.sender = createSender();
  }

  @Override
  public Mono<ESStreamMsg> pub(ESStreamMsg msg) {
    var record = this.toRecord(msg);
    return this.sender.send(Mono.just(record)).map(SenderResult::correlationMetadata).singleOrEmpty();
  }

  @Override
  public Flux<ESStreamMsg> sub(String topic, int partition) {
    return receive(topic, partition).map(KafkaUtils::toMsg);
  }

  private Flux<ReceiverRecord<String, String>> receive(String topic, int partition) {
    var tp = new TopicPartition(topic, partition);
    var receiverOptions = ReceiverOptions.<String, String>create(consumerConfig.toJavaMap())
                                         .subscription(singleton(topic))
                                         .addAssignListener(partitions -> partitions.forEach(p -> p.seek(0)))
                                         .assignment(singleton(tp));
    return KafkaReceiver.create(receiverOptions).receive();
  }

  private KafkaSender<String, String> createSender() {
    var senderOptions = SenderOptions.<String, String>create(producerConfig.toJavaMap());
    return KafkaSender.create(senderOptions);
  }

  private SenderRecord<String, String, ESStreamMsg> toRecord(ESStreamMsg ESStreamMsg) {
    return SenderRecord.create(ESStreamMsg.topic(),
                               ESStreamMsg.partition(),
                               timeSupplier.get(),
                               ESStreamMsg.key(),
                               ESStreamMsg.value(),
                               ESStreamMsg);
  }
}

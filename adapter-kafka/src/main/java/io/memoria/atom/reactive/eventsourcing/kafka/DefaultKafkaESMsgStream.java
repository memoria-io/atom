package io.memoria.atom.reactive.eventsourcing.kafka;

import io.memoria.atom.eventsourcing.pipeline.stream.ESMsg;
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

class DefaultKafkaESMsgStream implements KafkaESMsgStream {
  public final Map<String, Object> producerConfig;
  public final Map<String, Object> consumerConfig;
  private final Supplier<Long> timeSupplier;
  private final KafkaSender<String, String> sender;

  DefaultKafkaESMsgStream(Map<String, Object> producerConfig,
                          Map<String, Object> consumerConfig,
                          Supplier<Long> timeSupplier) {
    this.producerConfig = producerConfig;
    this.consumerConfig = consumerConfig;
    this.timeSupplier = timeSupplier;
    this.sender = createSender();
  }

  @Override
  public Mono<ESMsg> pub(ESMsg msg) {
    var record = this.toRecord(msg);
    return this.sender.send(Mono.just(record)).map(SenderResult::correlationMetadata).singleOrEmpty();
  }

  @Override
  public Flux<ESMsg> sub(String topic, int partition) {
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

  private SenderRecord<String, String, ESMsg> toRecord(ESMsg ESMsg) {
    return SenderRecord.create(ESMsg.topic(), ESMsg.partition(), timeSupplier.get(), ESMsg.key(), ESMsg.value(), ESMsg);
  }
}

package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsg;
import io.nats.client.*;
import io.nats.client.api.StreamInfo;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;

import static io.memoria.atom.reactive.eventsourcing.nats.Config.DEFAULT_FETCH_WAIT;
import static io.memoria.atom.reactive.eventsourcing.nats.Utils.createOrUpdateStream;
import static io.memoria.atom.reactive.eventsourcing.nats.Utils.jetStreamSub;
import static io.memoria.atom.reactive.eventsourcing.nats.Utils.jetStreamSubLatest;

class DefaultNatsESMsgStream implements NatsESMsgStream {
  private static final Logger log = LoggerFactory.getLogger(DefaultNatsESMsgStream.class.getName());
  private final Config config;
  private final Connection nc;
  private final JetStream js;

  DefaultNatsESMsgStream(Config config) throws IOException, InterruptedException {
    this.config = config;
    this.nc = Nats.connect(Utils.toOptions(config));
    this.js = nc.jetStream();
    config.topics()
          .map(Utils::toStreamConfiguration)
          .map(c -> createOrUpdateStream(nc, c))
          .map(Try::get)
          .map(StreamInfo::toString)
          .forEach(log::info);
  }

  @Override
  public Mono<ESMsg> pub(ESMsg msg) {
    return Mono.fromCallable(() -> Utils.publishMsg(js, msg)).flatMap(Mono::fromFuture).thenReturn(msg);
  }

  @Override
  public Flux<ESMsg> sub(String topic, int partition) {
    var tp = Topic.create(topic, partition);
    var waitMillis = config.find(topic).map(TPConfig::fetchWaitMillis).getOrElse(DEFAULT_FETCH_WAIT);
    return Mono.fromCallable(() -> jetStreamSub(js, tp, 1))
               .flatMapMany(sub -> this.fetch(sub, waitMillis).repeat())
               .map(Utils::toMsg);
  }

  @Override
  public Mono<ESMsg> getLast(String topic, int partition) {
    var tp = Topic.create(topic, partition);
    var waitMillis = config.find(topic).map(TPConfig::fetchWaitMillis).getOrElse(DEFAULT_FETCH_WAIT);
    return Mono.fromCallable(() -> jetStreamSubLatest(js, tp))
               .flatMap(sub -> this.fetch(sub, waitMillis).singleOrEmpty())
               .map(Utils::toMsg);
  }

  private Flux<Message> fetch(JetStreamSubscription sub, long wait) {
    return Flux.generate((SynchronousSink<Message> sink) -> Utils.fetchOnce(nc, sub, sink, wait));
  }
}

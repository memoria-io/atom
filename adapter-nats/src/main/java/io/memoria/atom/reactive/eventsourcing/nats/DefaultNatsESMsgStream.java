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
import java.time.Duration;

import static io.memoria.atom.reactive.eventsourcing.nats.NatsConfig.DEFAULT_FETCH_WAIT;
import static io.memoria.atom.reactive.eventsourcing.nats.Utils.createOrUpdateStream;
import static io.memoria.atom.reactive.eventsourcing.nats.Utils.jetStreamSub;
import static io.memoria.atom.reactive.eventsourcing.nats.Utils.jetStreamSubLatest;

class DefaultNatsESMsgStream implements NatsESMsgStream {
  private static final Logger log = LoggerFactory.getLogger(DefaultNatsESMsgStream.class.getName());
  private final NatsConfig natsConfig;
  private final Connection nc;
  private final JetStream js;

  DefaultNatsESMsgStream(NatsConfig natsConfig) throws IOException, InterruptedException {
    this.natsConfig = natsConfig;
    this.nc = Nats.connect(Utils.toOptions(natsConfig));
    this.js = nc.jetStream();
    natsConfig.topics()
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
    var waitMillis = natsConfig.find(topic).map(TopicConfig::fetchWaitMillis).getOrElse(DEFAULT_FETCH_WAIT);
    return Mono.fromCallable(() -> jetStreamSub(js, tp, 1))
               .flatMapMany(sub -> this.fetch(sub, waitMillis).repeat())
               .map(Utils::toMsg);
  }

  @Override
  public Mono<ESMsg> getLast(String topic, int partition) {
    var tp = Topic.create(topic, partition);
    var waitMillis = natsConfig.find(topic).map(TopicConfig::fetchWaitMillis).getOrElse(DEFAULT_FETCH_WAIT);
    return Mono.fromCallable(() -> jetStreamSubLatest(js, tp))
               .flatMap(sub -> this.pull(sub, waitMillis).singleOrEmpty())
               .map(Utils::toMsg);
  }

  private Flux<Message> pull(JetStreamSubscription sub, long wait) {
    return Flux.generate((SynchronousSink<Message> sink) -> {
      sub.pullNoWait(1);
      fetchOnce(nc, sub, sink, wait);
    });
  }

  private Flux<Message> fetch(JetStreamSubscription sub, long wait) {
    return Flux.generate((SynchronousSink<Message> sink) -> fetchOnce(nc, sub, sink, wait));
  }

  static void fetchOnce(Connection nc, JetStreamSubscription sub, SynchronousSink<Message> sink, long fetchWaitMillis) {
    try {
      nc.flushBuffer();
      var msg = sub.nextMessage(Duration.ofMillis(fetchWaitMillis));
      if (msg != null) {
        sink.next(msg);
        msg.ack();
      }
      sink.complete();
    } catch (IOException | InterruptedException e) {
      sink.error(e);
    }
  }
}

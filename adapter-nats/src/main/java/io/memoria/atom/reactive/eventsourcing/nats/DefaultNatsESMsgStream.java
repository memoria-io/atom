package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsg;
import io.nats.client.*;
import io.nats.client.api.StreamInfo;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.IOException;
import java.time.Duration;

import static io.memoria.atom.reactive.eventsourcing.nats.NatsUtils.createOrUpdateStream;
import static io.memoria.atom.reactive.eventsourcing.nats.NatsUtils.jetStreamSub;
import static io.memoria.atom.reactive.eventsourcing.nats.NatsUtils.jetStreamSubLatest;

class DefaultNatsESMsgStream implements NatsESMsgStream {
  private static final Logger log = LoggerFactory.getLogger(DefaultNatsESMsgStream.class.getName());
  private final NatsConfig natsConfig;
  private final Connection nc;
  private final JetStream js;

  DefaultNatsESMsgStream(NatsConfig natsConfig) throws IOException, InterruptedException {
    this.natsConfig = natsConfig;
    this.nc = Nats.connect(NatsUtils.toOptions(natsConfig));
    this.js = nc.jetStream();
    natsConfig.topics()
              .map(NatsUtils::toStreamConfiguration)
              .map(c -> createOrUpdateStream(nc, c))
              .map(Try::get)
              .map(StreamInfo::toString)
              .forEach(log::info);
  }

  @Override
  public Mono<ESMsg> pub(ESMsg msg) {
    return Mono.fromCallable(() -> NatsUtils.publishMsg(js, msg)).flatMap(Mono::fromFuture).thenReturn(msg);
  }

  @Override
  public Flux<ESMsg> sub(String topic, int partition) {
    var tp = Topic.create(topic, partition);
    var config = natsConfig.find(topic).get();
    return Mono.fromCallable(() -> jetStreamSub(js, tp, 1))
               .flatMapMany(sub -> this.fetch(sub, config.maxWaitBeforeRetry()).repeat())
               .map(NatsUtils::toMsg);
  }

  /**
   * @param topic
   * @param partition
   * @return last message after maxWait expired
   */
  @Override
  public Mono<ESMsg> fetchLast(String topic, int partition) {
    var tp = Topic.create(topic, partition);
    var config = natsConfig.find(topic).get();

    return Mono.fromCallable(() -> this.pullLast(tp,
                                                 config.maxBatchSize(),
                                                 config.maxWaitBeforeRetry(),
                                                 config.minRetriesOfFetchLast(),
                                                 config.maxRetriesOfFetchLast()))
               .filter(Option::isDefined)
               .map(Option::get)
               .map(NatsUtils::toMsg);
  }

  @Override
  public void close() throws Exception {
    this.nc.close();
  }

  private Option<Message> pullLast(Topic topic, int maxBatchSize, Duration maxWait, int minRetries, int maxRetries)
          throws IOException, JetStreamApiException {

    Option<Message> result = Option.none();

    var sub = jetStreamSubLatest(js, topic);
    nc.flushBuffer();

    while (maxRetries > 0) {
      var msgs = sub.fetch(maxBatchSize, maxWait.toMillis() / maxRetries);
      // System.out.printf("MsgSize:%d Retries:%d%n", msgs.size(), maxRetries);
      if (msgs.size() > 0) {
        result = Option.some(msgs.get(msgs.size() - 1));
      } else {
        if (minRetries > 0) {
          minRetries--;
        } else {
          return result;
        }
      }
      maxRetries--;
    }
    return result;
  }

  private Flux<Message> fetch(JetStreamSubscription sub, Duration waitMillis) {
    return Flux.generate((SynchronousSink<Message> sink) -> fetchOnce(nc, sub, sink, waitMillis));
  }

  static void fetchOnce(Connection nc,
                        JetStreamSubscription sub,
                        SynchronousSink<Message> sink,
                        Duration fetchWaitMillis) {
    try {
      nc.flushBuffer();
      var msg = sub.nextMessage(fetchWaitMillis);
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

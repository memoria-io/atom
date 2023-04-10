package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsg;
import io.nats.client.*;
import io.nats.client.api.*;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import io.vavr.collection.List;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

class Utils {
  public static final String ID_HEADER = "ID_HEADER";

  private Utils() {}

  static Try<StreamInfo> createOrUpdateStream(Connection nc, StreamConfiguration streamConfiguration) {
    return Try.of(() -> {
      var streamNames = nc.jetStreamManagement().getStreamNames();
      if (streamNames.contains(streamConfiguration.getName()))
        return nc.jetStreamManagement().updateStream(streamConfiguration);
      else
        return nc.jetStreamManagement().addStream(streamConfiguration);
    });
  }

  static ErrorListener errorListener() {
    return new ErrorListener() {
      @Override
      public void errorOccurred(Connection conn, String error) {
        ErrorListener.super.errorOccurred(conn, error);
      }
    };
  }

  static CompletableFuture<PublishAck> publishMsg(JetStream js, ESMsg msg) {
    var message = toMessage(msg);
    var opts = PublishOptions.builder().clearExpected().messageId(msg.key()).build();
    return js.publishAsync(message, opts);
  }

  static JetStreamSubscription jetStreamSub(JetStream js, Topic topic, long offset)
          throws IOException, JetStreamApiException {
    var config = ConsumerConfiguration.builder()
                                      .ackPolicy(AckPolicy.None)
                                      .startSequence(offset)
                                      .replayPolicy(ReplayPolicy.Instant)
                                      .deliverPolicy(DeliverPolicy.ByStartSequence)
                                      .build();
    var subscribeOptions = PushSubscribeOptions.builder()
                                               .ordered(true)
                                               .stream(topic.streamName())
                                               .configuration(config)
                                               .build();
    return js.subscribe(topic.subjectName(), subscribeOptions);
  }

  static JetStreamSubscription jetStreamSubLatest(JetStream js, Topic topic) throws IOException, JetStreamApiException {
    var config = ConsumerConfiguration.builder()
                                      .ackPolicy(AckPolicy.Explicit)
                                      .deliverPolicy(DeliverPolicy.LastPerSubject)
                                      .build();
    var subscribeOptions = PullSubscribeOptions.builder().stream(topic.streamName()).configuration(config).build();
    return js.subscribe(topic.subjectName(), subscribeOptions);
  }

  static long size(Connection nc, Topic topic) throws IOException, JetStreamApiException {
    return streamInfo(nc, topic.streamName()).map(StreamInfo::getStreamState)
                                             .map(StreamState::getSubjects)
                                             .flatMap(Option::of)
                                             .map(List::ofAll)
                                             .getOrElse(List::empty)
                                             .find(s -> s.getName().equals(topic.subjectName()))
                                             .map(Subject::getCount)
                                             .getOrElse(0L);
  }

  static Option<StreamInfo> streamInfo(Connection nc, String streamName) throws IOException, JetStreamApiException {
    try {
      var opts = StreamInfoOptions.allSubjects();
      return Option.some(nc.jetStreamManagement().getStreamInfo(streamName, opts));
    } catch (JetStreamApiException e) {
      if (e.getErrorCode() == 404) {
        return Option.none();
      } else {
        throw e;
      }
    }
  }

  static Message toMessage(ESMsg ESMsg) {
    var tp = Topic.fromMsg(ESMsg);
    var headers = new Headers();
    headers.add(ID_HEADER, ESMsg.key());
    return NatsMessage.builder().subject(tp.subjectName()).headers(headers).data(ESMsg.value()).build();
  }

  static ESMsg toMsg(Message message) {
    var value = new String(message.getData(), StandardCharsets.UTF_8);
    var tp = Topic.fromSubject(message.getSubject());
    return new ESMsg(tp.topic(), tp.partition(), message.getHeaders().getFirst(ID_HEADER), value);
  }

  static Options toOptions(NatsConfig natsConfig) {
    return new Options.Builder().server(natsConfig.url()).errorListener(errorListener()).build();
  }

  static StreamConfiguration toStreamConfiguration(TopicConfig c) {
    return StreamConfiguration.builder()
                              .storageType(c.storageType())
                              .denyDelete(c.denyDelete())
                              .denyPurge(c.denyPurge())
                              .name(c.topic().streamName())
                              .subjects(c.topic().subjectName())
                              .build();
  }
}

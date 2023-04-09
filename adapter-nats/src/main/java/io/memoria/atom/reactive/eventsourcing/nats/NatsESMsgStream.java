package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.eventsourcing.pipeline.stream.ESMsgStream;
import io.nats.client.JetStreamApiException;

import java.io.IOException;

public interface NatsESMsgStream extends ESMsgStream {

  static ESMsgStream create(Config config) throws IOException, InterruptedException, JetStreamApiException {
    return new DefaultNatsESMsgStream(config);
  }
}

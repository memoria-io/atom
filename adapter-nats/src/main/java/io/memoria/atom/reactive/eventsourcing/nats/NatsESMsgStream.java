package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsgStream;
import io.nats.client.JetStreamApiException;

import java.io.IOException;

public interface NatsESMsgStream extends ESMsgStream {

  static ESMsgStream create(NatsConfig natsConfig) throws IOException, InterruptedException, JetStreamApiException {
    return new DefaultNatsESMsgStream(natsConfig);
  }
}

package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.atom.core.stream.ESMsgStream;
import io.nats.client.Connection;

public interface NatsESMsgStream extends ESMsgStream {

  static NatsESMsgStream create(Connection nc, NatsConfig natsConfig) {
    return new DefaultNatsESMsgStream(nc, natsConfig);
  }
}

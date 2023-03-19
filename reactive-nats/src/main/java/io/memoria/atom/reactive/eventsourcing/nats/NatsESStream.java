package io.memoria.atom.reactive.eventsourcing.nats;

import io.memoria.reactive.eventsourcing.infra.stream.ESStream;
import io.nats.client.JetStreamApiException;

import java.io.IOException;

public interface NatsESStream extends ESStream {

  static ESStream create(Config config) throws IOException, InterruptedException, JetStreamApiException {
    return new DefaultNatsESStream(config);
  }
}

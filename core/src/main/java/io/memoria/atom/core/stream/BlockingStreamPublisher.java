package io.memoria.atom.core.stream;

import io.vavr.control.Try;

public interface BlockingStreamPublisher extends AutoCloseable {
  Try<Msg> publish(String topic, int partition, Msg msg);
}

package io.memoria.atom.core.stream;

import io.vavr.collection.Stream;
import io.vavr.control.Try;

public interface BlockingStreamSubscriber extends AutoCloseable {
  Try<Stream<Msg>> fetch(String topic, int partition);
}

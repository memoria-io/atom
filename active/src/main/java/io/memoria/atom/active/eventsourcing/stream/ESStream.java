package io.memoria.atom.active.eventsourcing.stream;

import io.vavr.control.Try;

import java.util.stream.Stream;

public interface ESStream {
  Try<ESStreamMsg> pub(ESStreamMsg esStreamMsg);

  Stream<ESStreamMsg> sub(String topic, int partition);
}
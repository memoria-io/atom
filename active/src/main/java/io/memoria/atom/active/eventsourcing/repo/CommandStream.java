package io.memoria.atom.active.eventsourcing.repo;

import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CommandStream {
  Try<CmdMsg> pub(CmdMsg cmd);

  Stream<CmdMsg> sub(String topic, int partition);
}

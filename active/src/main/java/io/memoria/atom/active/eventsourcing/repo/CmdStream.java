package io.memoria.atom.active.eventsourcing.repo;

import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CmdStream {
  Try<CmdMsg> pub(CmdMsg cmd);

  Stream<CmdMsg> sub(String topic, int partition);
}

package io.memoria.atom.active.eventsourcing.infra.cmd;

import io.vavr.control.Try;

import java.util.stream.Stream;

public interface CmdMsgStream {
  Try<CmdMsg> pub(CmdMsg cmd);

  Stream<CmdMsg> sub(String topic, int partition);
}

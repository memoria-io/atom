package io.memoria.atom.active.eventsourcing.infra.cmd;

import io.memoria.atom.active.eventsourcing.pipeline.CommandStream;
import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;

import java.util.stream.Stream;

public class CommandStreamAdapter<C extends Command> implements CommandStream<C> {
  private final CmdMsgStream stream;
  private final TextTransformer transformer;
  private final Class<C> cClass;

  public CommandStreamAdapter(CmdMsgStream stream, TextTransformer transformer, Class<C> cClass) {
    this.stream = stream;
    this.transformer = transformer;
    this.cClass = cClass;
  }

  public Try<C> pub(String topic, int partition, C c) {
    return transformer.serialize(c)
                      .map(cStr -> CmdMsg.create(topic, partition, c.commandId().value(), cStr))
                      .map(stream::pub)
                      .map(msg -> c);
  }

  public Stream<Try<C>> sub(String topic, int partition) {
    return stream.sub(topic, partition).map(msg -> transformer.deserialize(msg.value(), cClass));
  }
}

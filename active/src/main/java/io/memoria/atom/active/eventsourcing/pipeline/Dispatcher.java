package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.repo.CmdMsg;
import io.memoria.atom.active.eventsourcing.repo.CmdStream;
import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.core.eventsourcing.*;
import io.memoria.atom.core.text.TextTransformer;
import io.vavr.control.Try;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Dispatcher<S extends State, C extends Command, E extends Event> {
  private static final Logger log = LogManager.getLogger(Dispatcher.class.getSimpleName());

  private final Domain<S, C, E> domain;
  private final Route route;
  private final CmdStream cmdStream;
  private final EventRepo EventRepo;
  private final TextTransformer transformer;
  private final Map<StateId, Pipeline<S, C>> pipelines;

  public Dispatcher(Domain<S, C, E> domain,
                    Route route,
                    CmdStream cmdStream,
                    EventRepo EventRepo,
                    TextTransformer transformer) {
    this.domain = domain;
    this.route = route;
    this.cmdStream = cmdStream;
    this.EventRepo = EventRepo;
    this.transformer = transformer;
    this.pipelines = new ConcurrentHashMap<>();
  }

  public Stream<Try<Boolean>> dispatch() {
    return cmdStream.sub(route.cmdTopic(), route.cmdPartition()).map(this::handle);
  }

  private Try<Boolean> handle(CmdMsg cmdMsg) {
    var cmdTry = transformer.deserialize(cmdMsg.value(), domain.cClass());
    if (cmdTry.isFailure())
      return Try.failure(cmdTry.getCause());
    var cmd = cmdTry.get();
    pipelines.computeIfAbsent(cmd.stateId(), s -> {
      var pipeline = new StatePipeline<>(domain, route, cmdStream, EventRepo, transformer);
      Thread.startVirtualThread(() -> pipeline.stream().forEach(this::execute));
      return pipeline;
    });
    return pipelines.get(cmd.stateId()).offer(cmd);
  }

  private void execute(Try<S> tr) {
    if (tr.isSuccess()) {
      log.info(tr.get());
    } else {
      log.error("Pipeline Error:", tr.getCause());
    }
  }
}

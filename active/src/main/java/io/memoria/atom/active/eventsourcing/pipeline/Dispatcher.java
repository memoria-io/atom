package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.*;
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
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;
  private final Map<StateId, Pipeline<S, C, E>> pipelines;

  public Dispatcher(Domain<S, C, E> domain, Route route, CommandStream<C> commandStream, EventRepo<E> eventRepo) {
    this.domain = domain;
    this.route = route;
    this.commandStream = commandStream;
    this.eventRepo = eventRepo;
    this.pipelines = new ConcurrentHashMap<>();
  }

  public Stream<Try<C>> handle() {
    return commandStream.sub(route.cmdTopic(), route.cmdPartition()).map(cTry -> cTry.flatMap(this::append));
  }

  private Try<C> append(C cmd) {
    pipelines.computeIfAbsent(cmd.stateId(), s -> {
      var pipeline = new StatePipeline<>(domain, route, commandStream, eventRepo);
      Thread.startVirtualThread(() -> pipeline.stream().forEach(log::info));
      return pipeline;
    });
    return pipelines.get(cmd.stateId()).append(cmd).map(v -> cmd);
  }
}

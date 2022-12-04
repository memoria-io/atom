package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.stream.CommandStream;
import io.memoria.atom.active.eventsourcing.stream.ESStream;
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
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;
  private final Map<StateId, Pipeline<C, E>> pipelines;

  public Dispatcher(Domain<S, C, E> domain,
                    Route route,
                    ESStream esStream,
                    ESRepo esRepo,
                    TextTransformer transformer) {
    this.domain = domain;
    this.route = route;
    this.commandStream = CommandStream.from(esStream, transformer, domain.cClass());
    this.eventRepo = EventRepo.from(esRepo, transformer, domain.eClass());
    this.pipelines = new ConcurrentHashMap<>();
  }

  Dispatcher(Domain<S, C, E> domain, Route route, CommandStream<C> commandStream, EventRepo<E> eventRepo) {
    this.domain = domain;
    this.route = route;
    this.commandStream = commandStream;
    this.eventRepo = eventRepo;
    this.pipelines = new ConcurrentHashMap<>();
  }

  public Stream<Try<C>> run() {
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

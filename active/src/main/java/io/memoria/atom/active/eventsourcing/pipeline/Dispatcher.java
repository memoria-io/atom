package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.active.eventsourcing.repo.EventRepo;
import io.memoria.atom.active.eventsourcing.stream.CommandStream;
import io.memoria.atom.core.eventsourcing.*;
import io.vavr.control.Try;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Dispatcher<S extends State, C extends Command, E extends Event> {
  private final Domain<S, C, E> domain;
  private final Route route;
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;
  private final Consumer<Try<E>> resultConsumer;
  private final Map<StateId, Pipeline<C, E>> pipelines;

  public Dispatcher(Domain<S, C, E> domain,
                    Route route,
                    CommandStream<C> commandStream,
                    EventRepo<E> eventRepo,
                    Consumer<Try<E>> resultConsumer) {
    this.domain = domain;
    this.route = route;
    this.commandStream = commandStream;
    this.eventRepo = eventRepo;
    this.resultConsumer = resultConsumer;
    this.pipelines = new ConcurrentHashMap<>();
  }

  public Stream<Try<C>> run() {
    return commandStream.sub(route.cmdTopic(), route.cmdPartition()).map(cTry -> cTry.flatMap(this::append));
  }

  private Try<C> append(C cmd) {
    pipelines.computeIfAbsent(cmd.stateId(), s -> {
      var pipeline = new StatePipeline<>(domain, route, commandStream, eventRepo, resultConsumer);
      Thread.ofVirtual().name(threadName(cmd)).start(() -> pipeline.stream().forEachOrdered(resultConsumer));
      return pipeline;
    });
    return pipelines.get(cmd.stateId()).append(cmd).map(v -> cmd);
  }

  private String threadName(C cmd) {
    return "StateId=%s".formatted(cmd.stateId().value());
  }
}

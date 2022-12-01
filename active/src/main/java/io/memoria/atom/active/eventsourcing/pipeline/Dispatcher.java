package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.*;
import io.vavr.control.Try;

import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Function;
import java.util.stream.Stream;

public class Dispatcher<S extends State, C extends Command, E extends Event> {
  private final Domain<S, C, E> domain;
  private final Route route;
  private final CommandStream<C> commandStream;
  private final EventRepo<E> eventRepo;
  private final Map<StateId, Pipeline<S, C, E>> pipelines;
  private final BlockingDeque<Try<E>> eventQueue;

  public Dispatcher(Domain<S, C, E> domain, Route route, CommandStream<C> commandStream, EventRepo<E> eventRepo) {
    this.domain = domain;
    this.route = route;
    this.commandStream = commandStream;
    this.eventRepo = eventRepo;
    this.pipelines = new ConcurrentHashMap<>();
    this.eventQueue = new LinkedBlockingDeque<>();
  }

  public Stream<Try<Boolean>> dispatch() {
    return commandStream.sub(route.cmdTopic(), route.cmdPartition()).map(cTry -> cTry.flatMap(this::dispatch));
  }

  public Stream<Try<E>> stream() {
    return Stream.generate(()-> Try.of(eventQueue::take).flatMap(Function.identity()));
  }

  private Try<Boolean> dispatch(C cmd) {
    pipelines.computeIfAbsent(cmd.stateId(), s -> {
      var pipeline = new StatePipeline<>(domain, route, commandStream, eventRepo);
      Thread.startVirtualThread(() -> pipeline.stream().forEach(eventQueue::offer));
      return pipeline;
    });
    return pipelines.get(cmd.stateId()).offer(cmd);
  }
}

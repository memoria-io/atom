package io.memoria.atom.active.eventsourcing.aggregate;

import io.memoria.atom.core.eventsourcing.*;
import io.vavr.control.Try;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

public class AggregateFuture<S extends State, C extends Command, E extends Event> {
  private static final Logger log = LoggerFactory.getLogger(AggregateFuture.class.getSimpleName());
  private final StateId stateId;
  private final Aggregate<S, C, E> aggregate;
  private final FutureTask<Aggregate<S, C, E>> task;
  private final Thread thread;

  public AggregateFuture(StateId stateId,
                         Aggregate<S, C, E> aggregate,
                         Consumer<Try<E>> eventConsumer,
                         ThreadFactory factory) {
    this.stateId = stateId;
    this.aggregate = aggregate;
    this.task = new FutureTask<>(run(eventConsumer), aggregate);
    this.thread = factory.newThread(task);
  }

  public void start() {
    this.thread.start();
    log.info("Thread:%s started".formatted(thread.getName()));
  }

  public Try<C> append(C cmd) {
    return switch (task.state()) {
      case RUNNING -> aggregate.append(cmd);
      case SUCCESS -> Try.failure(new IllegalStateException(message("finished")));
      case FAILED -> Try.failure(task.exceptionNow());
      case CANCELLED -> Try.failure(new InterruptedException(message("cancelled")));
    };
  }

  public String name() {
    return aggregate.domain.toShortString() + ":" + aggregate.route.toShortString();
  }

  public void interrupt() {
    this.thread.interrupt();
    log.warn("Thread %s with stateId %s was interrupted".formatted(thread.getName(), stateId.value()));
  }

  private String message(String threadState) {
    return "AggregateFuture with thread %s with stateId %s was %s".formatted(name(), stateId.value(), threadState);
  }

  private Runnable run(Consumer<Try<E>> consumer) {
    return () -> aggregate.stream(stateId).forEachOrdered(consumer);
  }
}

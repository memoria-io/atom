package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.vavr.Function2;
import io.vavr.control.Option;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

public interface Evolver<S extends State, E extends Event> extends Function2<S, E, S> {
  S apply(E e);

  default Mono<S> reduce(Flux<E> events) {
    return events.reduce(Option.none(), this::applyOpt).filter(Option::isDefined).map(Option::get);
  }

  default Flux<S> accumulate(Flux<E> events) {
    var atomicReference = new AtomicReference<S>();
    return events.map(event -> {
      if (atomicReference.get() == null) {
        S newState = apply(event);
        atomicReference.compareAndExchange(null, newState);
        return newState;
      } else {
        var prevState = atomicReference.get();
        var newState = apply(prevState, event);
        atomicReference.compareAndExchange(prevState, newState);
        return newState;
      }
    });
  }

  private Option<S> applyOpt(Option<S> optState, E event) {
    return optState.map(s -> this.apply(s, event)).orElse(() -> Option.some(apply(event)));
  }
}

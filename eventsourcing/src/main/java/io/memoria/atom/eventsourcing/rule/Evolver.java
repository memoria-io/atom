package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.vavr.Function2;

public interface Evolver<S extends State, E extends Event> extends Function2<S, E, S> {
  S apply(E e);
}

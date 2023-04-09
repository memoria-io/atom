package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.vavr.Function1;

@FunctionalInterface
public interface Reducer<S extends State, E extends Event> extends Function1<S, E> {}

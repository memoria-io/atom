package io.memoria.atom.eventsourcing.rule;

import io.memoria.active.eventsourcing.Event;
import io.memoria.active.eventsourcing.State;
import io.vavr.Function2;

@FunctionalInterface
public interface Evolver<S extends State, E extends Event> extends Function2<S, E, S> {

}

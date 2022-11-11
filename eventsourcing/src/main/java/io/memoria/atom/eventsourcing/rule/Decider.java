package io.memoria.atom.eventsourcing.rule;

import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;
import io.vavr.Function2;
import io.vavr.control.Try;

@FunctionalInterface
public interface Decider<S extends State, C extends Command, E extends Event> extends Function2<S, C, Try<E>> {

}

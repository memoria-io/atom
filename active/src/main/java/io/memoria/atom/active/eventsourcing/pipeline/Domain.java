package io.memoria.atom.active.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Command;
import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.State;
import io.memoria.atom.core.eventsourcing.rule.Decider;
import io.memoria.atom.core.eventsourcing.rule.Evolver;
import io.memoria.atom.core.eventsourcing.rule.Saga;
import io.vavr.control.Option;

public record Domain<S extends State, C extends Command, E extends Event>(Class<S> sClass,
                                                                          Class<C> cClass,
                                                                          Class<E> eClass,
                                                                          S initState,
                                                                          Decider<S, C, E> decider,
                                                                          Saga<E, C> saga,
                                                                          Evolver<S, E> evolver) {
  public Domain(Class<S> sClass,
                Class<C> cClass,
                Class<E> eClass,
                S initState,
                Decider<S, C, E> decider,
                Evolver<S, E> evolver) {
    this(sClass, cClass, eClass, initState, decider, e -> Option.none(), evolver);
  }
}

package io.memoria.atom.eventsourcing;

import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.rule.Saga;

public record Domain<S extends State, C extends Command, E extends Event>(Class<S> sClass,
                                                                          Class<C> cClass,
                                                                          Class<E> eClass,
                                                                          Decider<S, C, E> decider,
                                                                          Evolver<S, E> evolver,
                                                                          Saga<E, C> saga) {
  public String toShortString() {
    return "Domain(%s,%s,%s)".formatted(sClass.getSimpleName(), cClass.getSimpleName(), eClass.getSimpleName());
  }
}

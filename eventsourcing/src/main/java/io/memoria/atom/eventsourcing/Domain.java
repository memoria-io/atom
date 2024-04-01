package io.memoria.atom.eventsourcing;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.rule.Saga;
import io.memoria.atom.eventsourcing.state.State;

public record Domain(Decider decider, Evolver evolver, Saga saga) {
  @Override
  public String toString() {
    return "Domain(%s,%s,%s)".formatted(State.class.getSimpleName(),
                                        Command.class.getSimpleName(),
                                        Event.class.getSimpleName());
  }
}

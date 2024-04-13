package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.ActorFactory;
import io.memoria.atom.eventsourcing.actor.StateAggregate;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.io.Closeable;
import java.util.Optional;

public interface ActorSystem extends Closeable, Iterable<StateAggregate> {
  ActorStore actorStore();

  ActorFactory actorFactory();

  Optional<Event> decide(StateId stateId, Command command) throws CommandException;

  Optional<State> evolve(StateId stateId, Event event);

  static ActorSystem create(ActorStore actorStore, ActorFactory actorFactory) {
    return new DefaultActorSystem(actorStore, actorFactory);
  }
}

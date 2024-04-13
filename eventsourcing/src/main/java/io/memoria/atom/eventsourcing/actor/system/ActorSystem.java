package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.ActorFactory;
import io.memoria.atom.eventsourcing.actor.StateActor;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.io.Closeable;

public interface ActorSystem extends Closeable, Iterable<StateActor> {
  ActorStore actorStore();

  ActorFactory actorFactory();

  Event decide(StateId stateId, Command command);

  State evolve(StateId stateId, Event event);

  static ActorSystem create(ActorStore actorStore, ActorFactory actorFactory) {
    return new DefaultActorSystem(actorStore, actorFactory);
  }
}

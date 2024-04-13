package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.ActorFactory;
import io.memoria.atom.eventsourcing.actor.StateActor;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.io.IOException;
import java.util.Iterator;

record DefaultActorSystem(ActorStore actorStore, ActorFactory actorFactory) implements ActorSystem {

  @Override
  public State evolve(StateId stateId, Event event) {
    actorStore.computeIfAbsent(stateId, actorFactory::create);
    return actorStore.get(stateId).evolve(stateId, event);
  }

  @Override
  public Event decide(StateId stateId, Command command) {
    actorStore.computeIfAbsent(stateId, actorFactory::create);
    return actorStore.get(stateId).decide(command);
  }

  @Override
  public void close() throws IOException {
    actorStore.close();
  }

  @Override
  public Iterator<StateActor> iterator() {
    return actorStore.iterator();
  }
}

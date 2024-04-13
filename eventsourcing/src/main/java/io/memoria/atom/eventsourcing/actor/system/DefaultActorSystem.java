package io.memoria.atom.eventsourcing.actor.system;

import io.memoria.atom.eventsourcing.actor.ActorFactory;
import io.memoria.atom.eventsourcing.actor.StateAggregate;
import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;

import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

record DefaultActorSystem(ActorStore actorStore, ActorFactory actorFactory) implements ActorSystem {

  @Override
  public Optional<State> evolve(StateId stateId, Event event) {
    actorStore.computeIfAbsent(stateId, actorFactory::create);
    return actorStore.get(stateId).evolve(event);
  }

  @Override
  public Optional<Event> decide(StateId stateId, Command command) throws CommandException {
    actorStore.computeIfAbsent(stateId, actorFactory::create);
    return actorStore.get(stateId).decide(command);
  }

  @Override
  public void close() throws IOException {
    actorStore.close();
  }

  @Override
  public Iterator<StateAggregate> iterator() {
    return actorStore.iterator();
  }
}

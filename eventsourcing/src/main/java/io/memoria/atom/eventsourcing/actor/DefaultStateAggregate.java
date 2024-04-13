package io.memoria.atom.eventsourcing.actor;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.exceptions.MismatchingEvent;
import io.memoria.atom.eventsourcing.rule.Decider;
import io.memoria.atom.eventsourcing.rule.Evolver;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class DefaultStateAggregate implements StateAggregate {
  private static final Logger log = LoggerFactory.getLogger(DefaultStateAggregate.class.getName());

  // Rules
  private final Decider decider;
  private final Evolver evolver;

  // State
  private final StateId stateId;
  private final AtomicReference<State> stateRef;
  private final AtomicReference<EventId> prevEventIdRef;
  private final Set<CommandId> processedCommands;
  private final Set<EventId> sagaSources;

  public DefaultStateAggregate(Decider decider, Evolver evolver, StateId stateId) {
    // Rules
    this.decider = decider;
    this.evolver = evolver;

    // State
    this.stateId = stateId;
    this.stateRef = new AtomicReference<>();
    this.processedCommands = new HashSet<>();
    this.prevEventIdRef = new AtomicReference<>();
    this.sagaSources = new HashSet<>();
  }

  @Override
  public StateId stateId() {
    return stateId;
  }

  @Override
  public Optional<Event> decide(Command command) throws CommandException {
    if (isDuplicate(command)) {
      return Optional.empty();
    }
    validate(command);
    Optional<Event> result;
    if (stateRef.get() == null) {
      result = Optional.of(decider.apply(command));
    } else {
      result = Optional.of(decider.apply(stateRef.get(), command));
    }
    command.meta().sagaSource().ifPresent(sagaSources::add);
    return result;
  }

  @Override
  public Optional<State> evolve(Event event) {
    if (isDuplicate(event)) {
      return Optional.empty();
    }
    validate(event);
    State currentState = stateRef.get();
    State newState;
    if (isInitializerEvent(event)) {
      newState = evolver.apply(event);
    } else {
      newState = evolver.apply(currentState, event);
    }
    stateRef.set(newState);
    processedCommands.add(event.meta().commandId());
    return Optional.of(newState);
  }

  boolean isDuplicate(Command command) {
    var alreadyProcessedCmd = processedCommands.contains(command.meta().commandId());
    var alreadyProcessedSagaCmd = command.meta().sagaSource().map(sagaSources::contains).orElse(false);
    return !alreadyProcessedCmd && !alreadyProcessedSagaCmd;
  }

  boolean isDuplicate(Event event) {
    return prevEventIdRef.get() != null && prevEventIdRef.get().equals(event.meta().eventId());
  }

  void validate(Event event) {
    // Check if matches stateId
    if (!stateId.equals(event.stateId())) {
      throw MismatchingEvent.of(stateId, event);
    }
    // Check if matches expected version
    long expectedVersion = Optional.ofNullable(stateRef.get()).map(State::version).map(v -> v + 1).orElse(0L);
    if (event.version() != expectedVersion) {
      throw MismatchingEvent.of(event, expectedVersion);
    }
  }

  void validate(Command command) {
    // Check if matches stateId
    if (!stateId.equals(command.stateId())) {
      throw MismatchingCommandState.of(stateId, command);
    }
  }

  boolean isInitializerEvent(Event event) {
    return event.version() == 0;
  }

}

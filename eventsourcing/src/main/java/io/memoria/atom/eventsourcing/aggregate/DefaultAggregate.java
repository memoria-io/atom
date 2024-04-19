package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.command.Command;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.command.exceptions.MismatchingCommandState;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.exceptions.MismatchingEvent;
import io.memoria.atom.eventsourcing.event.repo.EventRepo;
import io.memoria.atom.eventsourcing.state.State;
import io.memoria.atom.eventsourcing.state.StateId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

class DefaultAggregate implements Aggregate {
  private static final Logger log = LoggerFactory.getLogger(DefaultAggregate.class.getName());

  private final StateId stateId;
  // Rules
  private final Decider decider;
  private final Evolver evolver;
  private final EventRepo eventRepo;

  // State
  private final AtomicReference<State> stateRef;
  private final Set<CommandId> processedCommands;
  private final Set<EventId> sagaSources;

  public DefaultAggregate(StateId stateId, Decider decider, Evolver evolver, EventRepo eventRepo) {
    this.stateId = stateId;
    // Rules
    this.decider = decider;
    this.evolver = evolver;
    this.eventRepo = eventRepo;

    // State
    this.stateRef = new AtomicReference<>();
    this.processedCommands = new HashSet<>();
    this.sagaSources = new HashSet<>();
  }

  @Override
  public StateId stateId() {
    return this.stateId;
  }

  @Override
  public Optional<Event> handle(Command command) throws CommandException {
    initialize();
    // Validations should be after initialization
    if (isDuplicate(command)) {
      return Optional.empty();
    }
    validate(command);
    Event event;
    if (stateRef.get() == null) {
      // If still no state, expect a creation command
      event = decider.decide(command);
    } else {
      event = decider.decide(stateRef.get(), command);
    }
    eventRepo.append(event);
    command.meta().sagaSource().ifPresent(sagaSources::add);
    return Optional.of(event);
  }

  /**
   * Initialize if no initial state
   */
  private void initialize() {
    if (stateRef.get() == null) {
      eventRepo.fetch(stateId()).forEach(this::evolve);
    }
  }

  void evolve(Event event) {
    validate(event);
    State currentState = stateRef.get();
    State newState;
    if (isInitializerEvent(event)) {
      newState = evolver.evolve(event);
    } else {
      newState = evolver.evolve(currentState, event);
    }
    stateRef.set(newState);
    processedCommands.add(event.meta().commandId());
  }

  boolean isDuplicate(Command command) {
    var alreadyProcessedCmd = processedCommands.contains(command.meta().commandId());
    var alreadyProcessedSagaCmd = command.meta().sagaSource().map(sagaSources::contains).orElse(false);
    return alreadyProcessedCmd || alreadyProcessedSagaCmd;
  }

  void validate(Event event) {
    // Check if matches stateId
    if (!stateId().equals(event.stateId())) {
      throw MismatchingEvent.of(stateId(), event);
    }
    // Check if matches expected version
    long expectedVersion = Optional.ofNullable(stateRef.get()).map(State::version).map(v -> v + 1).orElse(0L);
    if (event.version() != expectedVersion) {
      throw MismatchingEvent.of(event, expectedVersion);
    }
  }

  void validate(Command command) {
    // Check if matches stateId
    if (!stateId().equals(command.stateId())) {
      throw MismatchingCommandState.of(stateId(), command);
    }
  }

  boolean isInitializerEvent(Event event) {
    return event.version() == 0;
  }

}

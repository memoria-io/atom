package io.memoria.atom.eventsourcing.exceptions;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.eventsourcing.Command;
import io.memoria.atom.eventsourcing.Event;
import io.memoria.atom.eventsourcing.State;

import static io.memoria.atom.eventsourcing.exceptions.InvalidEvolution.EvolutionType.EVOLUTION_COMMAND;
import static io.memoria.atom.eventsourcing.exceptions.InvalidEvolution.EvolutionType.EVOLUTION_EVENT;
import static io.memoria.atom.eventsourcing.exceptions.InvalidEvolution.EvolutionType.INITIAL_COMMAND;
import static io.memoria.atom.eventsourcing.exceptions.InvalidEvolution.EvolutionType.INITIAL_EVENT;

public class InvalidEvolution extends IllegalArgumentException implements ESException {
  public final EvolutionType evolutionType;

  protected InvalidEvolution(String msg, EvolutionType evolutionType) {
    super(msg);
    this.evolutionType = evolutionType;
  }

  public static InvalidEvolution of(Event event) {
    var msg = getInvalidInitialMsg(event);
    return new InvalidEvolution(msg, INITIAL_EVENT);
  }

  public static InvalidEvolution of(Command command) {
    return new InvalidEvolution(getInvalidInitialMsg(command), INITIAL_COMMAND);
  }

  public static InvalidEvolution of(Event event, State state) {
    return new InvalidEvolution(getInvalidEvolutionMsg(event, state), EVOLUTION_EVENT);
  }

  public static InvalidEvolution of(Command command, State state) {
    return new InvalidEvolution(getInvalidEvolutionMsg(command, state), EVOLUTION_COMMAND);
  }

  private static String getInvalidEvolutionMsg(Shardable shardable, State state) {
    String msg = "Invalid evolution event: %s[%s] to the state: %s[%s]";
    if (shardable instanceof Event event) {
      return msg.formatted(event.getClass().getSimpleName(),
                           event.meta(),
                           state.getClass().getSimpleName(),
                           state.meta());
    } else if (shardable instanceof Command command) {
      return msg.formatted(command.getClass().getSimpleName(),
                           command.meta(),
                           state.getClass().getSimpleName(),
                           state.meta());
    } else {
      throw new IllegalArgumentException("Unknown shardable:" + shardable);
    }
  }

  private static String getInvalidInitialMsg(Shardable shardable) {
    String msg = "Invalid initial %s: %s[%s] for state";
    if (shardable instanceof Event event) {
      return msg.formatted("event", event.getClass().getSimpleName(), event.meta());
    } else if (shardable instanceof Command command) {
      return msg.formatted("event", command.getClass().getSimpleName(), command.meta());
    } else {
      throw new IllegalArgumentException("Unknown shardable:" + shardable);
    }
  }

  public enum EvolutionType {
    INITIAL_EVENT,
    INITIAL_COMMAND,
    EVOLUTION_EVENT,
    EVOLUTION_COMMAND
  }
}

package io.memoria.atom.eventsourcing.command;

import io.memoria.atom.core.domain.Shardable;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.state.StateId;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public final class CommandMeta implements Shardable, Serializable {
  @Serial
  private static final long serialVersionUID = 0L;
  private final CommandId commandId;
  private final StateId stateId;
  private final long timestamp;
  private final EventId sagaSource;

  public CommandMeta(CommandId commandId, StateId stateId) {
    this(commandId, stateId, System.currentTimeMillis());
  }

  public CommandMeta(CommandId commandId, StateId stateId, long timestamp) {
    this(commandId, stateId, timestamp, null);
  }

  public CommandMeta(CommandId commandId, StateId stateId, long timestamp, EventId sagaSource) {
    this.commandId = Objects.requireNonNull(commandId);
    this.stateId = Objects.requireNonNull(stateId);
    this.timestamp = timestamp;
    this.sagaSource = sagaSource;
  }

  @Override
  public Id shardKey() {
    return stateId;
  }

  public CommandId commandId() {return commandId;}

  public StateId stateId() {return stateId;}

  public long timestamp() {return timestamp;}

  public Optional<EventId> sagaSource() {return Optional.ofNullable(sagaSource);}

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CommandMeta that = (CommandMeta) o;
    return timestamp == that.timestamp
           && Objects.equals(commandId, that.commandId)
           && Objects.equals(stateId,
                             that.stateId)
           && Objects.equals(sagaSource, that.sagaSource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(commandId, stateId, timestamp, sagaSource);
  }

  @Override
  public String toString() {
    return "CommandMeta["
           + "commandId="
           + commandId
           + ", "
           + "stateId="
           + stateId
           + ", "
           + "timestamp="
           + timestamp
           + ", "
           + "sagaSource="
           + sagaSource
           + ']';
  }
}


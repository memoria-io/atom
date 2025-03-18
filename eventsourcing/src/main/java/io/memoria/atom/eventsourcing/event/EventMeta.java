package io.memoria.atom.eventsourcing.event;

import io.memoria.atom.core.domain.Partitioned;
import io.memoria.atom.core.domain.Versioned;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.state.StateId;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

public class EventMeta implements Partitioned, Versioned, Serializable {
  @Serial
  private static final long serialVersionUID = 0L;
  private final EventId eventId;
  private final long version;
  private final StateId stateId;
  private final CommandId commandId;
  private final long timestamp;
  private final EventId sagaSource;

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId) {
    this(id, version, stateId, commandId, System.currentTimeMillis());
  }

  public EventMeta(EventId id, long version, StateId stateId, CommandId commandId, long timestamp) {
    this(id, version, stateId, commandId, timestamp, null);
  }

  public EventMeta(EventId eventId,
                   long version,
                   StateId stateId,
                   CommandId commandId,
                   long timestamp,
                   EventId sagaSource) {
    if (version < 0) {
      throw new IllegalArgumentException("version can't be less than zero");
    }
    this.eventId = Objects.requireNonNull(eventId);
    this.version = version;
    this.stateId = Objects.requireNonNull(stateId);
    this.commandId = Objects.requireNonNull(commandId);
    this.timestamp = timestamp;
    this.sagaSource = sagaSource;
  }

  @Override
  public Id pKey() {
    return stateId;
  }

  public EventId eventId() {return eventId;}

  @Override
  public long version() {return version;}

  public StateId stateId() {return stateId;}

  public CommandId commandId() {return commandId;}

  public long timestamp() {return timestamp;}

  public Optional<EventId> sagaSource() {return Optional.ofNullable(sagaSource);}

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    EventMeta eventMeta = (EventMeta) o;
    return version == eventMeta.version
           && timestamp == eventMeta.timestamp
           && Objects.equals(eventId,
                             eventMeta.eventId)
           && Objects.equals(stateId, eventMeta.stateId)
           && Objects.equals(commandId, eventMeta.commandId)
           && Objects.equals(sagaSource, eventMeta.sagaSource);
  }

  @Override
  public int hashCode() {
    return Objects.hash(eventId, version, stateId, commandId, timestamp, sagaSource);
  }

  @Override
  public String toString() {
    String msg = "EventMeta{eventId=%s, version=%d, stateId=%s, commandId=%s, timestamp=%d, sagaSource=%s}";
    return msg.formatted(eventId, version, stateId, commandId, timestamp, sagaSource);
  }
}


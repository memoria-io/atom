package io.memoria.atom.cassandra.eventsourcing;

import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.event.EventId;
import io.memoria.atom.eventsourcing.event.EventMeta;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.UUID;

public record SomeEvent(EventMeta meta) implements Event {
  public SomeEvent(long version, StateId stateId) {
    this(new EventMeta(EventId.of(UUID.randomUUID()), version, stateId, CommandId.of(UUID.randomUUID())));
  }
}

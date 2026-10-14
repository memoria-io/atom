package com.marmoush.atom.eventsourcing.aggregate;

import com.marmoush.atom.eventsourcing.aggregate.store.AggregateStore;
import com.marmoush.atom.eventsourcing.command.CommandIds;
import com.marmoush.atom.eventsourcing.command.CommandMeta;
import com.marmoush.atom.eventsourcing.command.exceptions.CommandException;
import com.marmoush.atom.eventsourcing.state.StateId;
import com.marmoush.atom.eventsourcing.state.StateIds;
import com.marmoush.atom.eventsourcing.usecase.simple.CreateState;
import com.marmoush.atom.eventsourcing.usecase.simple.StateCreated;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class AggregatesTest {
  private final Aggregates aggregates = Aggregates.create(AggregateStore.mapStore(), new DomainAggregateFactory());
  private final StateId stateId = StateIds.of(0);

  @ParameterizedTest
  @MethodSource("stores")
  void defaultSystem(AggregateStore aggregateStore) throws CommandException {
    // Given
    var meta = new CommandMeta(CommandIds.of(UUID.randomUUID()), stateId);
    // When
    var eventOpt = aggregates.handle(stateId, new CreateState(meta));
    // Then
    assertThat(eventOpt).isPresent().containsInstanceOf(StateCreated.class);
  }

  public static Stream<Arguments> stores() {
    return Stream.of(Arguments.of(Named.of("Concurrent map store", AggregateStore.mapStore())));
  }
}

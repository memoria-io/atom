package io.memoria.atom.eventsourcing.aggregate;

import io.memoria.atom.eventsourcing.aggregate.store.Store;
import io.memoria.atom.eventsourcing.command.CommandId;
import io.memoria.atom.eventsourcing.command.CommandMeta;
import io.memoria.atom.eventsourcing.command.exceptions.CommandException;
import io.memoria.atom.eventsourcing.data.CreateState;
import io.memoria.atom.eventsourcing.data.StateCreated;
import io.memoria.atom.eventsourcing.state.StateId;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.UUID;
import java.util.stream.Stream;

import static io.memoria.atom.eventsourcing.aggregate.Utils.cachedActorStore;
import static org.assertj.core.api.Assertions.assertThat;

public class AggregatesTest {
  private final Aggregates aggregates = Aggregates.create(Store.mapStore(), new DomainAggregateFactory());
  private final StateId stateId = StateId.of(0);

  @ParameterizedTest
  @MethodSource("stores")
  void defaultSystem(Store store) throws CommandException {
    // Given
    var meta = new CommandMeta(CommandId.of(UUID.randomUUID()), stateId);
    // When
    var eventOpt = aggregates.handle(stateId, new CreateState(meta));
    // Then
    assertThat(eventOpt).isPresent().containsInstanceOf(StateCreated.class);
  }

  public static Stream<Arguments> stores() {
    return Stream.of(Arguments.of(Named.of("Concurrent map store", Store.mapStore())),
                     Arguments.of(Named.of("Cache store", cachedActorStore("aggregateCache"))));
  }
}

package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.Utils;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateIds;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

class AggregateStoreTest {
  public static final int STATES_SIZE = 100;

  @ParameterizedTest
  @MethodSource("stores")
  void aggregateStore(AggregateStore aggregateStore) {
    // Given
    addAggregates(aggregateStore);

    // When
    stateIdStream().map(aggregateStore::get).map(Assertions::assertThat).forEach(AbstractAssert::isNotNull);
  }

  private static void addAggregates(AggregateStore aggregateStore) {
    stateIdStream().forEach(stateId -> aggregateStore.computeIfAbsent(stateId, Utils::simpleAggregate));
  }

  private static Stream<StateId> stateIdStream() {
    return IntStream.range(0, STATES_SIZE).mapToObj(StateIds::of);
  }

  public static Stream<Arguments> stores() {
    return Stream.of(Arguments.of(Named.of("Concurrent map store", AggregateStore.mapStore())));
  }
}

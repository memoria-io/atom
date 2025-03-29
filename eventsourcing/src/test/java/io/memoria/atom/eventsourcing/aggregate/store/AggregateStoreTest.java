package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.Utils;
import io.memoria.atom.eventsourcing.state.StateId;
import io.memoria.atom.eventsourcing.state.StateIds;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Duration;
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

  @Test
  void cachedInvalidationShouldWork() {
    // Given
    var aggregateStore = Utils.cachedAggregateStore("storesCache", 200);

    // When
    addAggregates(aggregateStore);

    // Then
    Awaitility.await().atMost(Duration.ofMillis(250)).until(() -> aggregateStore.get(StateIds.of(0)) == null);
  }

  private static void addAggregates(AggregateStore aggregateStore) {
    stateIdStream().forEach(stateId -> aggregateStore.computeIfAbsent(stateId, Utils::simpleAggregate));
  }

  private static Stream<StateId> stateIdStream() {
    return IntStream.range(0, STATES_SIZE).mapToObj(StateIds::of);
  }

  public static Stream<Arguments> stores() {
    return Stream.of(Arguments.of(Named.of("Concurrent map store", AggregateStore.mapStore())),
                     Arguments.of(Named.of("Cache store", Utils.cachedAggregateStore("CacheStore", 1000))));
  }
}

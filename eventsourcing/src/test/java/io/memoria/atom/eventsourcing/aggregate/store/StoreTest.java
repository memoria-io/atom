package io.memoria.atom.eventsourcing.aggregate.store;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.memoria.atom.eventsourcing.aggregate.Utils.cachedActorStore;

class StoreTest {
  @ParameterizedTest
  @MethodSource("stores")
  void get(Store store) {

  }

  public static Stream<Arguments> stores() {
    return Stream.of(Arguments.of(Named.of("Concurrent map store", Store.mapStore())),
                     Arguments.of(Named.of("Cache store", cachedActorStore("storesCache"))));
  }
}

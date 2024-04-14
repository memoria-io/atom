package io.memoria.atom.eventsourcing.aggregate.store;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class StoreTest {
  @ParameterizedTest
  @MethodSource("io.memoria.atom.eventsourcing.aggregate.Utils#stores")
  void get(Store store) {

  }
}

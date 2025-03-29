package io.memoria.atom.eventsourcing.state;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StateIdsTest {
  @Test
  void testOfString() {
    StateId id = StateIds.of("123");
    assertEquals("123", id.value());
  }

  @Test
  void testOfLong() {
    StateId id = StateIds.of(123L);
    assertEquals("123", id.value());
  }

  @Test
  void testOfUUID() {
    UUID uuid = UUID.randomUUID();
    StateId id = StateIds.of(uuid);
    assertEquals(uuid.toString(), id.value());
  }
}

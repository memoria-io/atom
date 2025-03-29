package io.memoria.atom.eventsourcing.event;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventIdsTest {
  @Test
  void testOfString() {
    EventId id = EventIds.of("123");
    assertEquals("123", id.value());
  }

  @Test
  void testOfLong() {
    EventId id = EventIds.of(123L);
    assertEquals("123", id.value());
  }

  @Test
  void testOfUUID() {
    UUID uuid = UUID.randomUUID();
    EventId id = EventIds.of(uuid);
    assertEquals(uuid.toString(), id.value());
  }
}

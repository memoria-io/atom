package io.memoria.atom.eventsourcing.command;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommandIdsTest {
  @Test
  void testOfString() {
    CommandId id = CommandIds.of("123");
    assertEquals("123", id.value());
  }

  @Test
  void testOfLong() {
    CommandId id = CommandIds.of(123L);
    assertEquals("123", id.value());
  }

  @Test
  void testOfUUID() {
    UUID uuid = UUID.randomUUID();
    CommandId id = CommandIds.of(uuid);
    assertEquals(uuid.toString(), id.value());
  }
}

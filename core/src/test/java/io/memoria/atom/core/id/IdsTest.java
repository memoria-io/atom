package io.memoria.atom.core.id;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdsTest {
  @Test
  void testOfString() {
    Id id = Ids.of("123");
    assertEquals("123", id.value());
  }

  @Test
  void testOfLong() {
    Id id = Ids.of(123L);
    assertEquals("123", id.value());
  }

  @Test
  void testOfUUID() {
    UUID uuid = UUID.randomUUID();
    Id id = Ids.of(uuid);
    assertEquals(uuid.toString(), id.value());
  }
}

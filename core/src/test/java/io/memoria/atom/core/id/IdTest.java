package io.memoria.atom.core.id;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class IdTest {

  @Test
  void validation() {
    UUID u = null;
    Assertions.assertThrows(NullPointerException.class, () -> Id.of(u));
    Assertions.assertThrows(IllegalArgumentException.class, () -> Id.of(""));
  }
}

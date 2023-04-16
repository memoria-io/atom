package io.memoria.atom.core.id.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TimedUUIDGeneratorTest {
  @Test
  void generation() {
    IdGenerator idGen = new TimedUUIDGenerator();
    Assertions.assertEquals(36, idGen.get().value().length());
    Assertions.assertEquals(5, idGen.get().value().split("-").length);
  }

  @Test
  void ordering() {
    //    IdGenerator idGen = new UUIDGenerator();
    //    TreeMap<Id, Integer> map = new TreeMap<>();
    //    List.range(0, 1000).forEach(i -> map.put(idGen.get(), i));
    //    map.entrySet().iterator().forEachRemaining(e -> System.out.println(e.getKey() + ":" + e.getValue()));
  }
}

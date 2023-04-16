package io.memoria.atom.core.id.generator;

import io.memoria.atom.core.id.Id;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

class SeqGeneratorTest {
  @Test
  void SerialIdTest() {
    IdGenerator idGen = IdGenerator.createSeqIdGenerator();
    Assertions.assertEquals("0", idGen.get().value());
    Assertions.assertEquals("1", idGen.get().value());
    Assertions.assertEquals("2", idGen.get().value());
    idGen = new SeqIdGenerator(new AtomicLong());
    Assertions.assertEquals("0", idGen.get().value());
  }

  @Test
  void UUIDTest() {
    IdGenerator idGen = new TimedUUIDGenerator();
    Assertions.assertEquals(36, idGen.get().value().length());
    Assertions.assertEquals(5, idGen.get().value().split("-").length);
  }

  @Test
  void idTest() {
    UUID u = null;
    Assertions.assertThrows(NullPointerException.class, () -> Id.of(u));
    Assertions.assertThrows(IllegalArgumentException.class, () -> Id.of(""));
  }

  @Test
  void testUUID() {
    //    IdGenerator idGen = new UUIDGenerator();
    //    TreeMap<Id, Integer> map = new TreeMap<>();
    //    List.range(0, 1000).forEach(i -> map.put(idGen.get(), i));
    //    map.entrySet().iterator().forEachRemaining(e -> System.out.println(e.getKey() + ":" + e.getValue()));
  }
}

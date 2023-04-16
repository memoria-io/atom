package io.memoria.atom.core.id;

import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

class IdTest {

  @Test
  void validation() {
    UUID nullUUID = null;
    var randomUUID = UUID.randomUUID();
    Assertions.assertThatExceptionOfType(NullPointerException.class).isThrownBy(() -> Id.of(nullUUID));
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Id.of(randomUUID));
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Id.of(-1L));
    Assertions.assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> Id.of(""));
  }

  @Test
  void uuidOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    List.range(0, 1000).forEach(i -> map.put(Id.of(), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }


  @Test
  void seqIdOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    List.range(0, 1000).forEach(i -> map.put(Id.of(i), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }
}

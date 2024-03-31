package io.memoria.atom.core.id;

import com.github.f4b6a3.uuid.UuidCreator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

class IdTest {
  @Test
  void checkToString() {
    Assertions.assertThat(new Id("id")).hasToString("id");
  }

  @Test
  void idEquality() {
    // Given
    var uuid = UUID.randomUUID();
    var uuidStr = uuid.toString();

    // When
    var id1 = Id.of(uuid);
    var id2 = Id.of(uuidStr);

    // Then
    Assertions.assertThat(id1).isEqualTo(id2).hasToString(uuidStr);
    Assertions.assertThat(id2).hasToString(uuidStr);
  }

  @Test
  void happyPath() {
    var id = Id.of(UuidCreator.getTimeOrderedEpoch());
    Assertions.assertThat(id.value()).isNotEmpty();
  }

  @Test
  void validation() {
    String str = null;
    //noinspection ConstantValue
    Assertions.assertThatNullPointerException().isThrownBy(() -> Id.of(str));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Id.of(-1L));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Id.of(""));
  }

  @Test
  void uuidOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(Id.of(UuidCreator.getTimeOrderedEpoch()), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }

  @Test
  void seqIdOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(Id.of(i), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }
}

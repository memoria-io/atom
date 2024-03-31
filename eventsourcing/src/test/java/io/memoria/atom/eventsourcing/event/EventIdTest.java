package io.memoria.atom.eventsourcing.event;

import com.github.f4b6a3.uuid.UuidCreator;
import io.memoria.atom.core.id.Id;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

class EventIdTest {
  @Test
  void checkToString() {
    Assertions.assertThat(new EventId("id")).hasToString("id");
  }

  @Test
  void happyPath() {
    var id = Id.of(UuidCreator.getTimeOrderedEpoch());
    Assertions.assertThat(id.value()).isNotEmpty();
  }

  @Test
  void idEquality() {
    // Given
    var uuid = UUID.randomUUID();
    var uuidStr = uuid.toString();

    // When
    var id1 = EventId.of(uuid);
    var id2 = EventId.of(uuidStr);

    // Then
    Assertions.assertThat(id1).isEqualTo(id2);
    Assertions.assertThat(id1.toString()).isEqualTo(uuidStr);
    Assertions.assertThat(id2.toString()).isEqualTo(uuidStr);
  }

  @Test
  void validation() {
    String str = null;
    //noinspection ConstantValue
    Assertions.assertThatNullPointerException().isThrownBy(() -> EventId.of(str));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> EventId.of(-1L));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> EventId.of(""));
  }

  @Test
  void uuidOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(EventId.of(UuidCreator.getTimeOrderedEpoch()), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }

  @Test
  void seqIdOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(EventId.of(i), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }
}

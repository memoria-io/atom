package io.memoria.atom.eventsourcing.event;

import com.github.f4b6a3.uuid.UuidCreator;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.Ids;
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
    var id = Ids.of(UuidCreator.getTimeOrderedEpoch());
    Assertions.assertThat(id.value()).isNotEmpty();
  }

  @Test
  void idEquality() {
    // Given
    var uuid = UUID.randomUUID();
    var uuidStr = uuid.toString();

    // When
    var id1 = EventIds.of(uuid);
    var id2 = EventIds.of(uuidStr);

    // Then
    Assertions.assertThat(id1).isEqualTo(id2).hasToString(uuidStr);
  }

  @Test
  void validation() {
    String str = null;
    //noinspection ConstantValue
    Assertions.assertThatNullPointerException().isThrownBy(() -> EventIds.of(str));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> EventIds.of(-1L));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> EventIds.of(""));
  }

  @Test
  void uuidOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(EventIds.of(UuidCreator.getTimeOrderedEpoch()), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }

  @Test
  void seqIdOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(EventIds.of(i), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }
}

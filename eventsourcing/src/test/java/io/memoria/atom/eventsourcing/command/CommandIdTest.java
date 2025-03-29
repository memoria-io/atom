package io.memoria.atom.eventsourcing.command;

import com.github.f4b6a3.uuid.UuidCreator;
import io.memoria.atom.core.id.Id;
import io.memoria.atom.core.id.Ids;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

class CommandIdTest {
  @Test
  void checkToString() {
    Assertions.assertThat(new CommandId("id")).hasToString("id");
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
    var id1 = CommandIds.of(uuid);
    var id2 = CommandIds.of(uuidStr);

    // Then
    Assertions.assertThat(id1).isEqualTo(id2).hasToString(uuidStr);
  }

  @Test
  void validation() {
    String str = null;
    //noinspection ConstantValue
    Assertions.assertThatNullPointerException().isThrownBy(() -> CommandIds.of(str));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> CommandIds.of(-1L));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> CommandIds.of(""));
  }

  @Test
  void uuidOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(CommandIds.of(UuidCreator.getTimeOrderedEpoch()), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }

  @Test
  void seqIdOrdering() {
    TreeMap<Id, Integer> map = new TreeMap<>();
    IntStream.range(0, 1000).forEach(i -> map.put(CommandIds.of(i), i));
    var atomic = new AtomicInteger(0);
    map.forEach((k, v) -> Assertions.assertThat(v).isEqualTo(atomic.getAndIncrement()));
  }
}

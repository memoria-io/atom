package io.memoria.atom.core.id;

import com.github.f4b6a3.uuid.UuidCreator;
import io.vavr.collection.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

class IdTest {
  @Test
  void happyPath(){
    var id = Id.of(UuidCreator.getTimeOrderedEpoch());
    Assertions.assertThat(id.value()).isNotEmpty();
  }

  @Test
  void implementationTypes(){
    Assertions.assertThat(Id.of("0")).isInstanceOf(SeqId.class);
    Assertions.assertThat(Id.of(UuidCreator.getTimeOrderedEpoch())).isInstanceOf(TimedUUID.class);
    Assertions.assertThat(Id.of("bob")).isInstanceOf(StringId.class);
  }

  @Test
  void validation() {
    UUID nullUUID = null;
    var randomUUID = UUID.randomUUID();
    Assertions.assertThatNullPointerException().isThrownBy(() -> Id.of(nullUUID));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Id.of(randomUUID));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Id.of(-1L));
    Assertions.assertThatIllegalArgumentException().isThrownBy(() -> Id.of(""));
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

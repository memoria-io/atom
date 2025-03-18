package io.memoria.atom.core.stream;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

class BlockingChainTest {
  private static final int count = 10_000;
  private final BlockingChain<Integer> stream = BlockingChain.inMemory();

  @Test
  @DisplayName("Stream single item")
  void streamOneElement() throws Exception {
    Thread.startVirtualThread(() -> {
      for (int i = 0; i < 1; i++) {
        stream.append(i);
      }
    });

    AtomicInteger idx = new AtomicInteger(0);
    var firstOpt = stream.fetch().findFirst();
    Assertions.assertThat(firstOpt).isPresent();
    Assertions.assertThat(firstOpt.get().call()).isEqualTo(idx.getAndIncrement());
  }

  @Test
  @DisplayName("Stream multiple items are in same order")
  void stream() throws Exception {
    Thread.startVirtualThread(() -> {
      for (int i = 0; i < count; i++) {
        stream.append(i);
      }
    });

    AtomicInteger idx = new AtomicInteger(0);
    var firstOpt = stream.fetch().findFirst();
    Assertions.assertThat(firstOpt).isPresent();
    Assertions.assertThat(firstOpt.get().call()).isEqualTo(idx.getAndIncrement());
  }

  @Test
  @DisplayName("Should block until tail is added")
  void tailBlocking() {
    Thread.startVirtualThread(() -> {
      stream.append(1);
      stream.append(2);
    });
    Awaitility.await().timeout(Duration.ofMillis(250)).until(() -> stream.fetch().limit(2).count() == 2);
  }
}

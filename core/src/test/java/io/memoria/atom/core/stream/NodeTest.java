package io.memoria.atom.core.stream;

import org.assertj.core.api.Assertions;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

class NodeTest {
  private final MemBlockingChain.Node<String> node = new MemBlockingChain.Node<>("hello");

  @Test
  @DisplayName("Should block until tail is added")
  void tailBlocking() {
    Thread.startVirtualThread(() -> {
      //      try {
      //        Thread.sleep(200);
      node.add(new MemBlockingChain.Node<>("world"));
      //      } catch (InterruptedException e) {
      //        throw new RuntimeException(e);
      //      }
    });
    Awaitility.await().timeout(Duration.ofMillis(250)).until(() -> node.tail().call().head().equals("world"));
  }

  @Test
  @DisplayName("Should only add tail once and never change")
  void tailCAS() throws Exception {
    for (int i = 0; i < 10; i++) {
      var result = node.add(new MemBlockingChain.Node<>("i=" + i));
      if (i == 0) {
        assert result;
      } else {
        assert !result;
      }
    }
    var head = node.tail().call().head();
    Assertions.assertThat(head).isEqualTo("i=0");
  }
}

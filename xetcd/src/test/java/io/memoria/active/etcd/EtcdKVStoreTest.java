package io.memoria.active.etcd;

import io.etcd.jetcd.Client;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class EtcdKVStoreTest {
  private final static Random random = new Random();
  private final static String keyPrefix = "key_" + random.nextInt(1000);
  private final Client client = Client.builder().endpoints("http://localhost:2379").build();
  private final EtcdKVStore kvStore = new EtcdKVStore(client, Duration.ofMillis(1000));

  @Test
  void getAndPut() {
    // Given
    int count = 100;
    var expectedValues = IntStream.range(0, count).mapToObj(EtcdKVStoreTest::toValue).toList();

    // When
    IntStream.range(0, count).forEach(this::setValue);
    var getKV = IntStream.range(0, count)
                         .mapToObj(EtcdKVStoreTest::toKey)
                         .map(this::getValue)
                         .map(EtcdKVStoreTest::handle)
                         .filter(Optional::isPresent)
                         .map(Optional::get)
                         .toList();

    // Then
    assertThat(getKV).hasSize(count).hasSameElementsAs(expectedValues);
  }

  private static Optional<String> handle(Callable<Optional<String>> s) {
    try {
      return s.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private Callable<Optional<String>> getValue(String value) {
    return kvStore.get(value);
  }

  @Test
  void notFound() throws Exception {
    assertThat(kvStore.get("some_value").call()).isEmpty();
  }

  private void setValue(int i) {
    try {
      kvStore.set(toKey(i), toValue(i)).call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private static String toKey(int i) {
    return keyPrefix + "_" + i;
  }

  private static String toValue(int i) {
    return "value:" + i;
  }
}

package io.memoria.atom.etcd;

import io.etcd.jetcd.Client;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class EtcdKVStoreTest {
  private final static Random random = new Random();
  private final static String keyPrefix = "key_" + random.nextInt(1000);
  private final Client client = Client.builder().endpoints("http://localhost:2379").build();
  private final EtcdKVStore kvStore = new EtcdKVStore(client);

  @Test
  void getAndPut() {
    // Given
    int count = 100;
    var expectedValues = IntStream.range(0, count).mapToObj(EtcdKVStoreTest::toValue).toList();

    // When
    IntStream.range(0, count).forEach(this::setValue);
    var getKV = IntStream.range(0, count).mapToObj(EtcdKVStoreTest::toKey).toList();

    // Then
    assertThat(getKV).hasSize(count).hasSameElementsAs(expectedValues);
  }

  @Test
  void notFound() throws Exception {
    assertThat(kvStore.get("some_value").get()).isEmpty();
  }

  private void setValue(int i) {
    try {
      kvStore.set(toKey(i), toValue(i)).get(1000, TimeUnit.MILLISECONDS);
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

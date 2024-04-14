package io.memoria.active.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.memoria.active.core.kv.KVStore;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EtcdKVStore implements KVStore {
  private final KV etcdKV;
  private final Duration timeout;

  public EtcdKVStore(Client client, Duration timeout) {
    this.etcdKV = client.getKVClient();
    this.timeout = timeout;
  }

  /**
   * @return first value of such key and ignores any other
   */
  @Override
  public Callable<Optional<String>> get(String key) {
    return () -> getValue(key);
  }

  /**
   * @return the value which was set
   */
  @Override
  public Callable<String> set(String key, String value) {
    return () -> {
      etcdKV.put(toByteSequence(key), toByteSequence(value)).get(timeout.toMillis(), TimeUnit.MILLISECONDS);
      return value;
    };
  }

  private Optional<String> getValue(String key) throws ExecutionException, InterruptedException, TimeoutException {
    return etcdKV.get(toByteSequence(key))
                 .get(timeout.toMillis(), TimeUnit.MILLISECONDS)
                 .getKvs()
                 .stream()
                 .findFirst()
                 .map(keyValue -> String.valueOf(keyValue.getValue()));
  }

  private static ByteSequence toByteSequence(String value) {
    return ByteSequence.from(value, StandardCharsets.UTF_8);
  }
}

package io.memoria.atom.etcd;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EtcdKVStore {
  private final KV etcdKV;

  public EtcdKVStore(Client client) {
    this.etcdKV = client.getKVClient();
  }

  public CompletableFuture<List<String>> get(String key) {
    return etcdKV.get(toByteSequence(key)).thenApply(GetResponse::getKvs).thenApply(this::extract);
  }

  public CompletableFuture<PutResponse> set(String key, String value) {
    return etcdKV.put(toByteSequence(key), toByteSequence(value));
  }

  static ByteSequence toByteSequence(String value) {
    return ByteSequence.from(value, StandardCharsets.UTF_8);
  }

  List<String> extract(List<KeyValue> keyValueList) {
    return keyValueList.stream().map(kv -> String.valueOf(kv.getValue())).toList();
  }
}

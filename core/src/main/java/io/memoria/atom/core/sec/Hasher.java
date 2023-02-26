package io.memoria.atom.core.sec;

public interface Hasher {
  String hash(String password, String salt);
}

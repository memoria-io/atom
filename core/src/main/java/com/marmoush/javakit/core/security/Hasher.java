package com.marmoush.javakit.core.security;

public interface Hasher {
  String hash(String password, String salt);
}

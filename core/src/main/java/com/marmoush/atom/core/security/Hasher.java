package com.marmoush.atom.core.security;

public interface Hasher {
  String hash(String password, String salt);
}

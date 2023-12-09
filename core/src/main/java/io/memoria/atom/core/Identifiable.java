package io.memoria.atom.core;

import io.memoria.atom.core.id.Id;

@FunctionalInterface
public interface Identifiable {
  Id id();
}

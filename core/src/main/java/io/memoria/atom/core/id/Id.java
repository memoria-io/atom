package io.memoria.atom.core.id;

import io.vavr.control.Try;

import java.io.Serializable;
import java.util.UUID;

public interface Id extends Serializable, Comparable<Id> {
  String value();

  /**
   * <p>
   * Creates UUID v7 generator based on <a
   * href="https://github.com/uuid6/uuid6-ietf-draft">https://github.com/uuid6/uuid6-ietf-draft</a>
   * </p>
   *
   * <p>
   * Based on library <a href="https://github.com/f4b6a3/uuid-creator"> https://github.com/f4b6a3/uuid-creator </a>
   * </p>
   */
  static Id of() {
    return new TimedUUID();
  }

  /**
   * @see #of()
   */
  static Id of(UUID id) {
    return new TimedUUID(id);
  }

  static Id of(long i) {
    return new SeqId(i);
  }

  /**
   * Tries to create Id implementation in the following order:
   * <ol>
   *  <li> {@link #of()} </li>
   *  <li> {@link #of(long)} </li>
   *  <li> new StringId(String) </li>
   * </ol>
   */
  static Id of(String value) {
    return Try.of(() -> of(UUID.fromString(value)))
              .orElse(Try.of(() -> of(Long.parseLong(value))))
              .getOrElse(new StringId(value));
  }
}
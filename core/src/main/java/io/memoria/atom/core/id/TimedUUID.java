package io.memoria.atom.core.id;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.util.UuidUtil;

import java.util.Objects;
import java.util.UUID;

public record TimedUUID(UUID uuid) implements Id {

  public TimedUUID {
    Objects.requireNonNull(uuid);
    if (!UuidUtil.isTimeOrderedEpoch(uuid)) {
      throw new IllegalArgumentException("uuid is not version 7");
    }
  }

  public TimedUUID() {
    this(UuidCreator.getTimeOrderedEpoch());
  }

  @Override
  public int compareTo(Id id) {
    return this.uuid.compareTo(UUID.fromString(id.value()));
  }

  @Override
  public String value() {
    return uuid.toString();
  }
}

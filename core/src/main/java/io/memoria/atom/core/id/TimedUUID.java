package io.memoria.atom.core.id;

import com.github.f4b6a3.uuid.UuidCreator;
import com.github.f4b6a3.uuid.util.UuidUtil;

import java.util.Objects;
import java.util.UUID;

public record TimedUUID(UUID uuidValue) implements IdValue {

  public TimedUUID {
    Objects.requireNonNull(uuidValue);
    if (!UuidUtil.isTimeOrderedEpoch(uuidValue)) {
      throw new IllegalArgumentException("uuid is not version 7");
    }
  }

  public TimedUUID() {
    this(UuidCreator.getTimeOrderedEpoch());
  }

  public TimedUUID(String value) {
    this(UUID.fromString(value));
  }

  @Override
  public String value() {
    return uuidValue.toString();
  }

  @Override
  public int compareTo(IdValue o) {
    if (o instanceof TimedUUID uuid) {
      return this.uuidValue.compareTo(uuid.uuidValue);
    } else {
      throw new IllegalArgumentException("Unable to compare current value:%s to other:%s ".formatted(this, o));
    }
  }
}

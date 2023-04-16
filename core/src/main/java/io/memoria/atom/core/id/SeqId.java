package io.memoria.atom.core.id;

public record SeqId(long i) implements Id {
  public SeqId {
    if (i < 0) {
      throw new IllegalArgumentException("Sequence Id value is less than 0");
    }
  }

  @Override
  public int compareTo(Id o) {
    return Long.compare(i, Long.parseLong(o.value()));
  }

  @Override
  public String value() {
    return String.valueOf(i);
  }
}

package io.memoria.atom.core.id;

public record SeqId(long seqValue) implements IdValue {
  public SeqId {
    if (seqValue < 0) {
      throw new IllegalArgumentException("Sequence Id value is less than 0");
    }
  }

  @Override
  public String value() {
    return String.valueOf(seqValue);
  }

  @Override
  public int compareTo(IdValue o) {
    if (o instanceof SeqId seqId) {
      return Long.compare(this.seqValue, seqId.seqValue);
    } else {
      throw new IllegalArgumentException("Unable to compare current value:%s to other:%s ".formatted(this, o));
    }
  }

  public static SeqId of(long value) {
    return new SeqId(value);
  }

  public static SeqId of(String value) {
    return new SeqId(Long.parseLong(value));
  }
}

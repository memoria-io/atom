package io.memoria.atom.core.id;

import java.io.Serializable;

public interface IdValue extends Serializable, Comparable<IdValue> {
  String value();
}

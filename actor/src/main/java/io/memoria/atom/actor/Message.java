package io.memoria.atom.actor;

import io.memoria.atom.core.Shardable;
import io.memoria.atom.core.id.Id;

public class Message implements Shardable {
  public Message(){

  }
  @Override
  public Id shardKey() {
    return null;
  }
}

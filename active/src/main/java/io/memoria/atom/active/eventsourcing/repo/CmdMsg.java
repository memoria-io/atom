package io.memoria.atom.active.eventsourcing.repo;

public interface CmdMsg {
  String key();

  String value();

  String topic();

  int partition();

  static CmdMsg create(String topic, int partition, String key, String value) {
    return new DefaultCmdMsg(topic, partition, key, value);
  }
}

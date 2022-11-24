package io.memoria.atom.active.eventsourcing.repo;

record DefaultCmdMsg(String topic, int partition, String key, String value) implements CmdMsg {}

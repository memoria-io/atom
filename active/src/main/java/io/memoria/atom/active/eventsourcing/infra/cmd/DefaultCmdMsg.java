package io.memoria.atom.active.eventsourcing.infra.cmd;

record DefaultCmdMsg(String topic, int partition, String key, String value) implements CmdMsg {}

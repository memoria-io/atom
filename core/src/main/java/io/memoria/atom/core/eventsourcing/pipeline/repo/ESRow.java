package io.memoria.atom.core.eventsourcing.pipeline.repo;

public record ESRow(String table, String stateId, int seqId, String value) {}

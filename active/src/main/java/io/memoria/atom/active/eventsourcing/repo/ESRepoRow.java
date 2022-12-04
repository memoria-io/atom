package io.memoria.atom.active.eventsourcing.repo;

public record ESRepoRow(String table, String stateId, int seqId, String value) {}

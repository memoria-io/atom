package io.memoria.atom.core.eventsourcing.infra.repo;

public record ESRepoRow(String table, String stateId, int seqId, String value) {}

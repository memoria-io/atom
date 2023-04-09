package io.memoria.atom.core.repo;

public record ESRow(String table, String stateId, int seqId, String value) {}

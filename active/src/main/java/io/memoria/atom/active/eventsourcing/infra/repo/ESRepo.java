package io.memoria.atom.active.eventsourcing.infra.repo;

import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import io.vavr.control.Option;
import io.vavr.control.Try;

import java.util.List;
import java.util.stream.Stream;

public interface ESRepo {
  Stream<ESRepoRow> getFirst(String table, StateId stateId);

  Stream<ESRepoRow> getAll(String table, StateId stateId);

  Stream<ESRepoRow> getAll(String table, StateId stateId, int minSeqId);

  Try<ESRepoRow> append(ESRepoRow esRepoRow);

  static ESRepo inMemory(List<String> topicNames) {
    return new MemESRepo(topicNames);
  }

  static ESRepo inMemory(String... topicNames) {
    return new MemESRepo(topicNames);
  }
}

package io.memoria.atom.active.eventsourcing.repo;

import io.vavr.control.Try;

import java.util.List;
import java.util.stream.Stream;

public interface ESRepo {
  Stream<ESRepoRow> getAll(String table, String stateId);

  Try<ESRepoRow> append(ESRepoRow esRepoRow);

  static ESRepo create(List<String> topicNames) {
    return new MemESRepo(topicNames);
  }

  static ESRepo create(String... topicNames) {
    return new MemESRepo(topicNames);
  }
}

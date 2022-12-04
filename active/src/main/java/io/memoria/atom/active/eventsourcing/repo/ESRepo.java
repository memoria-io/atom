package io.memoria.atom.active.eventsourcing.repo;

import io.vavr.control.Try;

import java.util.stream.Stream;

public interface ESRepo {
  Stream<ESRepoRow> getAll(String table, String stateId);

  Try<ESRepoRow> append(ESRepoRow esRepoRow);
}

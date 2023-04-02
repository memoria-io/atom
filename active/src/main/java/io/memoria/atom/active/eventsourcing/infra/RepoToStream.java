package io.memoria.atom.active.eventsourcing.infra;

import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.vavr.control.Try;

import java.util.stream.Stream;

public interface RepoToStream {
  Stream<Try<ESStreamMsg>> sync(String stateId);

  static RepoToStream create(ESRepo esRepo, ESStream esStream) {
    return new RepoToStreamImpl(route, esRepo, esStream);
  }
}

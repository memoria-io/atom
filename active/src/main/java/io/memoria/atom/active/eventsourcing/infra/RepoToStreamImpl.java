package io.memoria.atom.active.eventsourcing.infra;

import io.memoria.atom.active.eventsourcing.infra.repo.ESRepo;
import io.memoria.atom.active.eventsourcing.infra.stream.ESStream;
import io.memoria.atom.core.eventsourcing.Shardable;
import io.memoria.atom.core.eventsourcing.StateId;
import io.memoria.atom.core.eventsourcing.infra.QRoute;
import io.memoria.atom.core.eventsourcing.infra.repo.ESRepoRow;
import io.memoria.atom.core.eventsourcing.infra.stream.ESStreamMsg;
import io.vavr.control.Try;

import java.util.stream.Stream;

class RepoToStreamImpl implements RepoToStream {
  private final QRoute qRoute;
  private final ESRepo esRepo;
  private final ESStream esStream;

  public RepoToStreamImpl(QRoute qRoute, ESRepo esRepo, ESStream esStream) {
    this.qRoute = qRoute;
    this.esRepo = esRepo;
    this.esStream = esStream;
  }

  @Override
  public Stream<Try<ESStreamMsg>> sync(String stateId) {
    return esRepo.getAll(qRoute.eventTable(), stateId).map(row -> esStream.pub(toESStreamMsg(row)));
  }

  private ESStreamMsg toESStreamMsg(ESRepoRow row) {
    var partition = Shardable.partition(StateId.of(row.stateId()),qRoute.eventTopicTotalPartitions());
    new ESStreamMsg(qRoute.eventTopic(), partition,  )
    return null;
  }
}

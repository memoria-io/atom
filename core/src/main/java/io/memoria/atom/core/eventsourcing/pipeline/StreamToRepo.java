package io.memoria.atom.core.eventsourcing.pipeline;

import io.memoria.atom.core.eventsourcing.Event;
import io.memoria.atom.core.eventsourcing.pipeline.repo.ESRowRepo;
import io.memoria.atom.core.eventsourcing.pipeline.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import reactor.core.publisher.Flux;

public interface StreamToRepo<E extends Event> {
  Flux<E> sync();

  static <E extends Event> StreamToRepo<E> create(String tableName,
                                                  String topicName,
                                                  int partition,
                                                  ESMsgStream esMsgStream,
                                                  ESRowRepo esRowRepo,
                                                  TextTransformer transformer,
                                                  Class<E> eClass) {
    return new StreamToRepoImpl<>(tableName, topicName, partition, esMsgStream, esRowRepo, transformer, eClass);
  }
}

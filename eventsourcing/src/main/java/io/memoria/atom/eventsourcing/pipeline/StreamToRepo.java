package io.memoria.atom.eventsourcing.pipeline;

import io.memoria.atom.core.repo.ESRowRepo;
import io.memoria.atom.core.stream.ESMsgStream;
import io.memoria.atom.core.text.TextTransformer;
import io.memoria.atom.eventsourcing.Event;
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

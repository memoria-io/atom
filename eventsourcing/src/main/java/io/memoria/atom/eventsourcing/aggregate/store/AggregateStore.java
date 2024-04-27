package io.memoria.atom.eventsourcing.aggregate.store;

import io.memoria.atom.eventsourcing.aggregate.Aggregate;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface AggregateStore extends Iterable<Aggregate> {
  void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> actorFn);

  Aggregate get(StateId stateId);

  void remove(StateId stateId);

  static AggregateStore mapStore() {
    return new MemAggregateStore(new ConcurrentHashMap<>());
  }

  static AggregateStore mapStore(Map<StateId, Aggregate> map) {
    return new MemAggregateStore(map);
  }
}

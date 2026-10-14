package com.marmoush.javakit.eventsourcing.aggregate.store;

import com.marmoush.javakit.eventsourcing.aggregate.Aggregate;
import com.marmoush.javakit.eventsourcing.state.StateId;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public interface AggregateStore extends Iterable<Aggregate> {
  void computeIfAbsent(StateId stateId, Function<StateId, Aggregate> lazyActorFn);

  Aggregate get(StateId stateId);

  void remove(StateId stateId);

  void clear();

  static AggregateStore mapStore() {
    return new MemAggregateStore(new ConcurrentHashMap<>());
  }

  static AggregateStore mapStore(Map<StateId, Aggregate> map) {
    return new MemAggregateStore(map);
  }
}

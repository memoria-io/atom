package com.marmoush.javakit.eventsourcing.event.repo;

import com.marmoush.javakit.eventsourcing.event.Event;
import com.marmoush.javakit.eventsourcing.state.StateId;

import java.util.List;
import java.util.Map;

public interface EventRepo {
  void append(Event event);

  List<Event> fetch(StateId stateId);

  long size(StateId stateId);

  static EventRepo inMemory() {
    return new MemEventRepo();
  }

  static EventRepo inMemory(Map<StateId, List<Event>> db) {
    return new MemEventRepo(db);
  }
}

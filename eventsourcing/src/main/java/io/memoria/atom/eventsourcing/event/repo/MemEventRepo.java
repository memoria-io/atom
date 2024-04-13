package io.memoria.atom.eventsourcing.event.repo;

import io.memoria.atom.eventsourcing.event.Event;
import io.memoria.atom.eventsourcing.state.StateId;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class MemEventRepo implements EventRepo {
  private final Map<StateId, List<Event>> db;

  public MemEventRepo() {
    this(new ConcurrentHashMap<>());
  }

  public MemEventRepo(Map<StateId, List<Event>> db) {
    this.db = db;
  }

  @Override
  public void append(Event event) {
    db.computeIfPresent(event.stateId(), (_, v) -> {
      v.add(event);
      return v;
    });
    db.computeIfAbsent(event.stateId(), _ -> {
      var list = new ArrayList<Event>();
      list.add(event);
      return list;
    });
  }

  @Override
  public List<Event> fetch(StateId stateId) {
    var list = db.get(stateId);
    if (list == null) {
      return List.of();
    } else {
      return list;
    }
  }

  @Override
  public long size(StateId stateId) {
    return fetch(stateId).size();
  }
}

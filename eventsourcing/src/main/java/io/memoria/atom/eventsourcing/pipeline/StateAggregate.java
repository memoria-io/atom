package io.memoria.atom.eventsourcing.pipeline;

import io.memoria.atom.eventsourcing.State;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

class StateAggregate<S extends State> {
  private final AtomicReference<S> state = new AtomicReference<>();
  private final AtomicInteger eventSeqId = new AtomicInteger(0);

  public static <S extends State> StateAggregate<S> of(S state) {
    var st = new StateAggregate<S>();
    st.updateState(state);
    return st;
  }

  private StateAggregate() {
  }

  public S getState() {
    return this.state.get();
  }

  public void updateState(S s) {
    this.state.set(s);
    this.eventSeqId.incrementAndGet();
  }

  public int getEventSeqId() {
    return this.eventSeqId.get();
  }
}

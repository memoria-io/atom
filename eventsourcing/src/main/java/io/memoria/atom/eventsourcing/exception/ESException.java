package io.memoria.atom.eventsourcing.exception;

import io.memoria.atom.eventsourcing.*;

public interface ESException {
  class InvalidCommand extends IllegalArgumentException implements ESException {
    private InvalidCommand(String msg) {
      super(msg);
    }

    public static InvalidCommand create(Command command) {
      var msg = "Invalid initializer command (%s)".formatted(command.getClass().getSimpleName());
      return new InvalidCommand(msg);
    }

    public static InvalidCommand create(State state, Command command) {
      var msg = "Invalid command (%s) for the state (%s)".formatted(command.getClass().getSimpleName(),
                                                                    state.getClass().getSimpleName());
      return new InvalidCommand(msg);
    }
  }

  class InvalidEvent extends IllegalArgumentException implements ESException {

    private InvalidEvent(String msg) {
      super(msg);
    }

    public static InvalidEvent of(Event event) {
      var msg = "Invalid creator event:%s for creating state, this should never happen";
      return new InvalidEvent(msg.formatted(event.getClass().getSimpleName()));
    }

    public static InvalidEvent of(State state, Event event) {
      var msg = "Invalid evolution of: %s on current state: %s, this should never happen";
      return new InvalidEvent(msg.formatted(state.getClass().getSimpleName(), event.getClass().getSimpleName()));
    }
  }

  class MismatchingStateId extends Exception implements ESException {
    private MismatchingStateId(String msg) {
      super(msg);
    }

    public static MismatchingStateId of(StateId stateId, StateId cmdStateId) {
      var msg = "The Command's stateId:%s doesn't match stream stateId:%s";
      return new MismatchingStateId(msg.formatted(cmdStateId.value(), stateId.value()));
    }
  }

  class MismatchingEventSeqId extends Exception implements ESException {
    private MismatchingEventSeqId(String msg) {
      super(msg);
    }

    public static MismatchingStateId of(int seqId) {
      var msg = "The expected event sequence id: %d already exists";
      return new MismatchingStateId(msg.formatted(seqId));
    }

    public static MismatchingStateId of(int expectedSeqId, int actualSeqId) {
      var msg = "The expected event sequence id: %d doesn't match actual sequence id: %d";
      return new MismatchingStateId(msg.formatted(expectedSeqId, actualSeqId));
    }
  }
}

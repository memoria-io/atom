package io.memoria.atom.core.stream;

public interface BlockingStream extends BlockingStreamPublisher, BlockingStreamSubscriber {
  static BlockingStream inMemory() {
    return new MemBlockingStream();
  }
}

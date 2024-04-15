package io.memoria.atom.core.stream;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

/**
 * This combines BlockingDeque and iterables where it Blocks on next element, while it keeps all elements in memory for
 * replaying, and streams any new added elements to listeners.
 */
class MemBlockingChain<T> implements BlockingChain<T> {
  private final ReentrantLock lock;
  private final CountDownLatch latch;
  private final AtomicReference<Node<T>> first;
  private final AtomicReference<Node<T>> last;

  public MemBlockingChain() {
    this.lock = new ReentrantLock();
    this.latch = new CountDownLatch(1);
    this.first = new AtomicReference<>();
    this.last = new AtomicReference<>();
  }

  @Override
  public void append(T msg) {
    Objects.requireNonNull(msg);
    this.lock.lock();
    var node = new Node<>(msg);
    var casFirst = this.first.compareAndSet(null, node);
    if (casFirst) {
      this.last.set(node);
      this.latch.countDown();
    } else {
      this.last.get().add(node);
      this.last.set(node);
    }
    this.lock.unlock();
  }

  @Override
  public Stream<Callable<T>> fetch() {
    return Stream.iterate(getFirstNode(), tNode -> () -> tNode.call().tail().call()).map(c -> () -> c.call().head);
  }

  private Callable<Node<T>> getFirstNode() {
    return () -> {
      latch.await();
      return first.get();
    };
  }

  static class Node<T> {
    private final T head;
    private final CountDownLatch countDownLatch;
    private final AtomicReference<Node<T>> next;

    public Node(T head) {
      this.head = head;
      this.countDownLatch = new CountDownLatch(1);
      this.next = new AtomicReference<>();
    }

    public boolean add(Node<T> next) {
      var cas = this.next.compareAndSet(null, next);
      if (cas)
        this.countDownLatch.countDown();
      return cas;
    }

    public T head() {
      return head;
    }

    public Callable<Node<T>> tail() {
      return () -> {
        this.countDownLatch.await();
        return this.next.get();
      };
    }
  }
}

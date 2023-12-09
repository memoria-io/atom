package io.memoria.atom.actor;

import io.memoria.atom.core.id.Id;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;

public class SyncTest {
  private final int count = 100;
  private final CountDownLatch latch = new CountDownLatch(count);

  @Test
  void syncTest() throws InterruptedException {
    var h = new MyActor(latch);
    for (int i = 0; i < count; i++) {
      Thread.ofVirtual().name("Thread:" + i).start(() -> h.apply(new Message()));
    }
    latch.await();
  }

  static class MyActor extends AbstractActor {
    private final CountDownLatch latch;
    private volatile int count;

    MyActor(CountDownLatch latch) {
      super(Id.of());
      this.latch = latch;
      this.count = 0;
    }

    @Override
    public Id shardKey() {
      return null;
    }

    public int getCount() {
      return count;
    }

    @Override
    public synchronized Try<Message> apply(Message message) {
      //      try {
      System.out.println(STR. "Hello from: \{ Thread.currentThread().getName() } Count is now: \{ count }" );
      count++;
      //        Thread.sleep(r.nextInt(1000));
      count--;
      latch.countDown();
      System.out.println(STR. "Hello from: \{ Thread.currentThread().getName() } Count is now: \{ count }" );
      return Try.success(message);
      //      } catch (InterruptedException e) {
      //        return Try.failure(e);
      //      }
    }
  }
}

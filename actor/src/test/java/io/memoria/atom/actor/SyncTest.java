package io.memoria.atom.actor;

import io.memoria.atom.core.id.Id;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class SyncTest {
  @Test
  void syncTest() throws InterruptedException {
    var h = new MyActor();
    for (int i = 0; i < 100; i++) {
      Thread.ofVirtual().name("Thread:" + i).start(() -> h.apply(new Message()));
    }
    Thread.currentThread().join();
  }

  static class MyActor implements Actor {
    private static final Random r = new Random();
    private volatile int count;

    MyActor() {
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
      try {
        System.out.println(STR. "Hello from: \{ Thread.currentThread().getName() } Count is now: \{ count }" );
        count++;
        Thread.sleep(r.nextInt(1000));
        System.out.println(STR. "Hello from: \{ Thread.currentThread().getName() } Count is now: \{ count }" );
        return Try.success(message);
      } catch (InterruptedException e) {
        return Try.failure(e);
      }
    }
  }
}

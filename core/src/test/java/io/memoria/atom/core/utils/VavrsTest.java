package io.memoria.atom.core.utils;

import io.vavr.collection.List;
import io.vavr.control.Try;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

class VavrsTest {
  private final Try<List<Integer>> success = Try.of(() -> List.of(1, 2, 3));
  private final Exception e = new Exception();
  private final Try<List<Integer>> failure = Try.failure(e);

  @Test
  void handleTest() throws ExecutionException, InterruptedException {
    var success = CompletableFuture.completedFuture("success");
    Assertions.assertEquals(Try.success("success"), success.handle(Vavrs.handle()).get());

    var e = new Exception("failure");
    var failure = CompletableFuture.failedFuture(e);
    Assertions.assertEquals(Try.failure(e), failure.handle(Vavrs.handle()).get());
  }

  @Test
  void handleToVoidTest() throws ExecutionException, InterruptedException {
    var success = CompletableFuture.completedFuture("success");
    Assertions.assertEquals(Try.success(null), success.handle(Vavrs.handleToVoid()).get());

    var e = new Exception("failure");
    var failure = CompletableFuture.failedFuture(e);
    Assertions.assertEquals(Try.failure(e), failure.handle(Vavrs.handleToVoid()).get());
  }

  @Test
  void listOfTryTest() {
    List<Try<Integer>> su = List.ofAll(Vavrs.listOfTry(success));
    List<Try<Integer>> fa = List.ofAll(Vavrs.listOfTry(failure));
    Assertions.assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    Assertions.assertEquals(List.of(Try.failure(e)), fa);
  }

  @Test
  void traverseOfTryTest() {
    List<Try<Integer>> su = List.ofAll(Vavrs.traverseOfTry(success));
    List<Try<Integer>> fa = List.ofAll(Vavrs.traverseOfTry(failure));
    Assertions.assertEquals(List.of(Try.success(1), Try.success(2), Try.success(3)), su);
    Assertions.assertEquals(List.of(Try.failure(e)), fa);
  }

}

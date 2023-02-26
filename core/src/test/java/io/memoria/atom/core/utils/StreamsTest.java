package io.memoria.atom.core.utils;

import io.vavr.collection.List;
import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

class StreamsTest {
  @Test
  void merge() {
    var n = 1000;
    var s1 = IntStream.range(0, n).boxed();
    var s2 = IntStream.range(0, n).boxed();
    var s3 = IntStream.range(0, n).boxed();
    var c = Streams.merge(List.of(s1, s2, s3)).limit(3 * n).count();
    assert c == 3 * n;
  }
}
